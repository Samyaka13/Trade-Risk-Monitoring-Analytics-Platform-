package com.riskmanagement.service.impl;

import com.riskmanagement.dto.response.PnLHistoryResponse;
import com.riskmanagement.entity.PnLHistory;
import com.riskmanagement.entity.Trader;
import com.riskmanagement.exception.ResourceNotFoundException;
import com.riskmanagement.mapper.PnLHistoryMapper;
import com.riskmanagement.repository.PnLHistoryRepository;
import com.riskmanagement.repository.TradeRepository;
import com.riskmanagement.repository.TraderRepository;
import com.riskmanagement.service.PnLService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * PnL Service — manages daily PnL snapshots.
 * <p>
 * In real banks, end-of-day (EOD) PnL is computed by the official books & records
 * system and represents the official profit/loss for regulatory and management reporting.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PnLServiceImpl implements PnLService {

    private final PnLHistoryRepository pnlHistoryRepository;
    private final TradeRepository tradeRepository;
    private final TraderRepository traderRepository;
    private final PnLHistoryMapper pnlHistoryMapper;

    /**
     * Record the daily PnL snapshot for a trader.
     * Combines unrealized + realized PnL for the daily figure.
     */
    @Override
    public void recordDailyPnL(UUID traderId) {
        Trader trader = traderRepository.findById(traderId)
                .orElseThrow(() -> new ResourceNotFoundException("Trader", "id", traderId));

        BigDecimal unrealizedPnl = tradeRepository.calculateUnrealizedPnL(traderId);
        BigDecimal realizedPnl = tradeRepository.calculateRealizedPnL(traderId);
        BigDecimal dailyPnl = unrealizedPnl.add(realizedPnl);

        // Calculate cumulative PnL (sum of all daily PnLs to date)
        List<PnLHistory> history = pnlHistoryRepository.findByTraderIdOrderByDateDesc(traderId);
        BigDecimal previousCumulative = history.isEmpty()
                ? BigDecimal.ZERO
                : history.get(0).getCumulativePnl();
        BigDecimal cumulativePnl = previousCumulative.add(dailyPnl);

        PnLHistory pnlSnapshot = PnLHistory.builder()
                .trader(trader)
                .date(LocalDate.now())
                .dailyPnl(dailyPnl)
                .cumulativePnl(cumulativePnl)
                .build();

        pnlHistoryRepository.save(pnlSnapshot);
        log.info("Daily PnL recorded for trader {}: daily={}, cumulative={}",
                trader.getName(), dailyPnl, cumulativePnl);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PnLHistoryResponse> getPnLHistory(UUID traderId) {
        if (!traderRepository.existsById(traderId)) {
            throw new ResourceNotFoundException("Trader", "id", traderId);
        }
        List<PnLHistory> history = pnlHistoryRepository.findByTraderIdOrderByDateDesc(traderId);
        return pnlHistoryMapper.toResponseList(history);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PnLHistoryResponse> getPnLHistoryBetween(UUID traderId, LocalDate start, LocalDate end) {
        if (!traderRepository.existsById(traderId)) {
            throw new ResourceNotFoundException("Trader", "id", traderId);
        }
        List<PnLHistory> history = pnlHistoryRepository.findByTraderIdAndDateBetween(traderId, start, end);
        return pnlHistoryMapper.toResponseList(history);
    }
}
