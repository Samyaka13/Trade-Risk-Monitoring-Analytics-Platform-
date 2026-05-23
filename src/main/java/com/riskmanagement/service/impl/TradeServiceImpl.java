package com.riskmanagement.service.impl;

import com.riskmanagement.dto.request.TradeCreateRequest;
import com.riskmanagement.dto.request.TradeUpdateRequest;
import com.riskmanagement.dto.response.TradeResponse;
import com.riskmanagement.entity.Trade;
import com.riskmanagement.entity.Trader;
import com.riskmanagement.entity.enums.TradeStatus;
import com.riskmanagement.entity.enums.TradeType;
import com.riskmanagement.exception.ResourceNotFoundException;
import com.riskmanagement.exception.TradeValidationException;
import com.riskmanagement.mapper.TradeMapper;
import com.riskmanagement.repository.TradeRepository;
import com.riskmanagement.repository.TraderRepository;
import com.riskmanagement.riskengine.RiskEngineService;
import com.riskmanagement.service.PositionService;
import com.riskmanagement.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Trade Capture Module — handles the full trade lifecycle.
 * <p>
 * In investment banking, the trade capture system is one of the most critical components.
 * Every trade must be:
 * 1. Validated (correct instrument, valid counterparty, within risk limits)
 * 2. Booked (persisted with full audit trail)
 * 3. Enriched (linked to positions, risk calculations triggered)
 * 4. Settled (on settlement date, cash and securities are exchanged)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final TraderRepository traderRepository;
    private final TradeMapper tradeMapper;
    private final PositionService positionService;
    private final RiskEngineService riskEngineService;

    /**
     * Create and book a new trade.
     * <p>
     * Trade workflow:
     * 1. Validate trader exists and is active
     * 2. Create trade record with OPEN status
     * 3. Set current price = entry price (at trade inception, MTM = trade price)
     * 4. Calculate settlement date (T+2 for stocks, T+1 for futures)
     * 5. Recalculate positions for the trader+asset
     * 6. Trigger risk engine recalculation
     */
    @Override
    public TradeResponse createTrade(TradeCreateRequest request) {
        log.info("Creating trade: {} {} {} for trader {}",
                request.getTradeType(), request.getQuantity(), request.getAssetSymbol(), request.getTraderId());

        // 1. Validate trader
        Trader trader = traderRepository.findById(request.getTraderId())
                .orElseThrow(() -> new ResourceNotFoundException("Trader", "id", request.getTraderId()));

        if (!trader.getActive()) {
            throw new TradeValidationException("Cannot create trade for inactive trader: " + trader.getName());
        }

        // 2. Build trade entity
        Trade trade = Trade.builder()
                .trader(trader)
                .assetSymbol(request.getAssetSymbol().toUpperCase())
                .assetType(request.getAssetType())
                .tradeType(request.getTradeType())
                .instrumentType(request.getInstrumentType())
                .quantity(request.getQuantity())
                .entryPrice(request.getEntryPrice())
                .currentPrice(request.getEntryPrice()) // At inception, current = entry
                .tradeStatus(TradeStatus.OPEN)
                .counterparty(request.getCounterparty())
                .tradeDate(LocalDateTime.now())
                .settlementDate(calculateSettlementDate(request))
                .build();

        Trade savedTrade = tradeRepository.save(trade);
        log.info("Trade booked successfully: {}", savedTrade.getId());

        // 3. Recalculate position for this trader+asset
        positionService.recalculatePositions(trader.getId(), trade.getAssetSymbol());

        // 4. Trigger risk engine recalculation
        riskEngineService.recalculateRiskMetrics(trader.getId());

        return tradeMapper.toResponse(savedTrade);
    }

    /**
     * Update a trade's mutable fields.
     * Only OPEN trades can be modified. Closed/cancelled trades are immutable.
     */
    @Override
    public TradeResponse updateTrade(UUID tradeId, TradeUpdateRequest request) {
        Trade trade = findTradeOrThrow(tradeId);

        if (trade.getTradeStatus() != TradeStatus.OPEN) {
            throw new TradeValidationException(
                    "Cannot modify trade in status: " + trade.getTradeStatus());
        }

        if (request.getCurrentPrice() != null) {
            trade.setCurrentPrice(request.getCurrentPrice());
        }
        if (request.getQuantity() != null) {
            trade.setQuantity(request.getQuantity());
        }
        if (request.getCounterparty() != null) {
            trade.setCounterparty(request.getCounterparty());
        }

        Trade updatedTrade = tradeRepository.save(trade);

        // Recalculate downstream
        positionService.recalculatePositions(trade.getTrader().getId(), trade.getAssetSymbol());
        riskEngineService.recalculateRiskMetrics(trade.getTrader().getId());

        log.info("Trade updated: {}", tradeId);
        return tradeMapper.toResponse(updatedTrade);
    }

    /**
     * Close a trade — transition from OPEN to CLOSED.
     * <p>
     * When closing a trade:
     * 1. The current market price becomes the exit price
     * 2. Realized PnL is locked in: (exitPrice - entryPrice) × quantity
     * 3. The trade no longer contributes to unrealized PnL or exposure
     * 4. Position is recalculated to reflect the closed trade
     */
    @Override
    public TradeResponse closeTrade(UUID tradeId) {
        Trade trade = findTradeOrThrow(tradeId);

        if (trade.getTradeStatus() == TradeStatus.CLOSED) {
            throw new TradeValidationException("Trade is already closed");
        }
        if (trade.getTradeStatus() == TradeStatus.CANCELLED) {
            throw new TradeValidationException("Cannot close a cancelled trade");
        }

        trade.setTradeStatus(TradeStatus.CLOSED);
        trade.setExitPrice(trade.getCurrentPrice());
        trade.setClosedAt(LocalDateTime.now());

        Trade closedTrade = tradeRepository.save(trade);

        // Recalculate — closed trade is excluded from open position
        positionService.recalculatePositions(trade.getTrader().getId(), trade.getAssetSymbol());
        riskEngineService.recalculateRiskMetrics(trade.getTrader().getId());

        BigDecimal realizedPnl = trade.getTradeType() == TradeType.BUY
                ? trade.getExitPrice().subtract(trade.getEntryPrice()).multiply(trade.getQuantity())
                : trade.getEntryPrice().subtract(trade.getExitPrice()).multiply(trade.getQuantity());

        log.info("Trade closed: {}. Realized PnL: {}", tradeId, realizedPnl);
        return tradeMapper.toResponse(closedTrade);
    }

    @Override
    @Transactional(readOnly = true)
    public TradeResponse getTradeById(UUID tradeId) {
        Trade trade = findTradeOrThrow(tradeId);
        return tradeMapper.toResponse(trade);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TradeResponse> getAllTrades(Pageable pageable) {
        return tradeRepository.findAll(pageable)
                .map(tradeMapper::toResponse);
    }

    // ---- Private helpers ----

    private Trade findTradeOrThrow(UUID tradeId) {
        return tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Trade", "id", tradeId));
    }

    /**
     * Settlement date calculation.
     * - Stocks: T+2 (two business days after trade date)
     * - Futures: typically T+1
     * - Options: T+1
     * <p>
     * Simplified to calendar days here; production systems use business day calendars
     * that account for holidays and weekends per exchange.
     */
    private LocalDate calculateSettlementDate(TradeCreateRequest request) {
        return switch (request.getInstrumentType()) {
            case STOCK -> LocalDate.now().plusDays(2);
            case FUTURE, OPTION -> LocalDate.now().plusDays(1);
        };
    }
}
