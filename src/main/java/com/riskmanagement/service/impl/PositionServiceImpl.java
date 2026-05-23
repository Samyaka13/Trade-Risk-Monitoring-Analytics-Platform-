package com.riskmanagement.service.impl;

import com.riskmanagement.dto.response.PositionResponse;
import com.riskmanagement.entity.Position;
import com.riskmanagement.entity.Trade;
import com.riskmanagement.entity.Trader;
import com.riskmanagement.entity.enums.InstrumentType;
import com.riskmanagement.entity.enums.TradeStatus;
import com.riskmanagement.entity.enums.TradeType;
import com.riskmanagement.exception.ResourceNotFoundException;
import com.riskmanagement.mapper.PositionMapper;
import com.riskmanagement.repository.PositionRepository;
import com.riskmanagement.repository.TradeRepository;
import com.riskmanagement.repository.TraderRepository;
import com.riskmanagement.service.PositionService;
import com.riskmanagement.util.FinancialCalculations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Position Management Module — aggregates trades into net positions.
 * <p>
 * A position represents the net result of all open trades in a specific instrument
 * for a given trader. This is how trading floors monitor what each trader actually "owns."
 * <p>
 * Key concepts implemented:
 * - Net quantity: BUY trades add, SELL trades subtract
 * - Weighted average price: recalculated on every trade
 * - Market value: net quantity × current price (mark-to-market)
 * - Unrealized PnL: the paper profit/loss at current market prices
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final TradeRepository tradeRepository;
    private final TraderRepository traderRepository;
    private final PositionMapper positionMapper;

    /**
     * Recalculate the net position for a trader+asset combination.
     * <p>
     * Algorithm:
     * 1. Fetch all OPEN/PARTIALLY_FILLED trades for the trader+asset
     * 2. Calculate net quantity: Σ(BUY quantities) - Σ(SELL quantities)
     * 3. Calculate volume-weighted average entry price
     * 4. Calculate market value at current prices
     * 5. Calculate unrealized PnL
     */
    @Override
    public void recalculatePositions(UUID traderId, String assetSymbol) {
        log.debug("Recalculating position for trader {} asset {}", traderId, assetSymbol);

        Trader trader = traderRepository.findById(traderId)
                .orElseThrow(() -> new ResourceNotFoundException("Trader", "id", traderId));

        List<Trade> openTrades = tradeRepository.findByTraderIdAndAssetSymbolAndTradeStatusIn(
                traderId, assetSymbol, List.of(TradeStatus.OPEN, TradeStatus.PARTIALLY_FILLED));

        if (openTrades.isEmpty()) {
            // No open trades — remove position if it exists
            positionRepository.findByTraderIdAndAssetSymbolAndInstrumentType(
                    traderId, assetSymbol, InstrumentType.STOCK)
                    .ifPresent(positionRepository::delete);
            log.debug("No open trades for {} {} — position removed", traderId, assetSymbol);
            return;
        }

        // Determine instrument type from first trade
        InstrumentType instrumentType = openTrades.get(0).getInstrumentType();

        // Calculate net quantity and weighted average price
        BigDecimal netQuantity = BigDecimal.ZERO;
        BigDecimal totalBuyCost = BigDecimal.ZERO;
        BigDecimal totalBuyQty = BigDecimal.ZERO;
        BigDecimal latestPrice = openTrades.get(0).getCurrentPrice();

        for (Trade trade : openTrades) {
            latestPrice = trade.getCurrentPrice(); // Use the most recent current price

            if (trade.getTradeType() == TradeType.BUY) {
                netQuantity = netQuantity.add(trade.getQuantity());
                totalBuyCost = totalBuyCost.add(trade.getEntryPrice().multiply(trade.getQuantity()));
                totalBuyQty = totalBuyQty.add(trade.getQuantity());
            } else {
                netQuantity = netQuantity.subtract(trade.getQuantity());
            }
        }

        // Weighted average price (based on BUY trades)
        BigDecimal avgPrice = totalBuyQty.compareTo(BigDecimal.ZERO) > 0
                ? totalBuyCost.divide(totalBuyQty, 4, RoundingMode.HALF_UP)
                : latestPrice;

        // Market value = |net quantity| × current price
        BigDecimal marketValue = FinancialCalculations.calculateMarketValue(netQuantity, latestPrice);

        // Unrealized PnL = (current price - avg price) × net quantity
        BigDecimal unrealizedPnl = latestPrice.subtract(avgPrice).multiply(netQuantity);

        // Upsert position
        Position position = positionRepository.findByTraderIdAndAssetSymbolAndInstrumentType(
                traderId, assetSymbol, instrumentType)
                .orElse(Position.builder()
                        .trader(trader)
                        .assetSymbol(assetSymbol)
                        .instrumentType(instrumentType)
                        .build());

        position.setNetQuantity(netQuantity);
        position.setAveragePrice(avgPrice);
        position.setCurrentPrice(latestPrice);
        position.setMarketValue(marketValue);
        position.setUnrealizedPnl(unrealizedPnl);
        position.setLastUpdated(LocalDateTime.now());

        positionRepository.save(position);
        log.debug("Position updated: {} {} — Net Qty: {}, MV: {}, PnL: {}",
                traderId, assetSymbol, netQuantity, marketValue, unrealizedPnl);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionResponse> getPositionsByTrader(UUID traderId) {
        if (!traderRepository.existsById(traderId)) {
            throw new ResourceNotFoundException("Trader", "id", traderId);
        }
        List<Position> positions = positionRepository.findByTraderId(traderId);
        return positionMapper.toResponseList(positions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionResponse> getLargestPositions(int limit) {
        // Use the JPA-based approach for cross-database compatibility
        List<Position> allPositions = positionRepository.findAll();
        return allPositions.stream()
                .sorted((a, b) -> b.getMarketValue().abs().compareTo(a.getMarketValue().abs()))
                .limit(limit)
                .map(positionMapper::toResponse)
                .toList();
    }
}
