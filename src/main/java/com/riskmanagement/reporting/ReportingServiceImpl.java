package com.riskmanagement.reporting;

import com.riskmanagement.dto.response.ExposureSummaryResponse;
import com.riskmanagement.dto.response.PnLSummaryResponse;
import com.riskmanagement.dto.response.TopRiskyTraderResponse;
import com.riskmanagement.entity.RiskMetrics;
import com.riskmanagement.entity.Trader;
import com.riskmanagement.repository.*;
import com.riskmanagement.util.FinancialCalculations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Reporting Module Implementation.
 * <p>
 * Generates reports similar to what risk managers and desk heads review
 * at investment banks during morning risk meetings and end-of-day reviews.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportingServiceImpl implements ReportingService {

    private final TraderRepository traderRepository;
    private final RiskMetricsRepository riskMetricsRepository;
    private final PositionRepository positionRepository;
    private final PnLHistoryRepository pnlHistoryRepository;
    private final TradeRepository tradeRepository;

    /**
     * Get top risky traders — ranked by exposure-to-limit utilization ratio.
     * <p>
     * This is a key report reviewed in morning risk meetings.
     * Traders near or above their limits require immediate attention.
     */
    @Override
    public List<TopRiskyTraderResponse> getTopRiskyTraders(int limit) {
        List<Trader> activeTraders = traderRepository.findByActiveTrue();

        List<TopRiskyTraderResponse> result = new ArrayList<>();
        for (Trader trader : activeTraders) {
            Optional<RiskMetrics> metricsOpt = riskMetricsRepository.findByTraderId(trader.getId());
            if (metricsOpt.isPresent()) {
                RiskMetrics metrics = metricsOpt.get();
                BigDecimal utilization = FinancialCalculations.calculateUtilizationPercentage(
                        metrics.getTotalExposure(), trader.getRiskLimit());

                result.add(TopRiskyTraderResponse.builder()
                        .traderId(trader.getId())
                        .name(trader.getName())
                        .desk(trader.getDesk().name())
                        .totalExposure(metrics.getTotalExposure())
                        .riskLimit(trader.getRiskLimit())
                        .utilizationPercentage(utilization)
                        .totalPnl(metrics.getTotalPnl())
                        .varEstimate(metrics.getVarEstimate())
                        .breachStatus(metrics.getBreachStatus())
                        .build());
            }
        }

        // Sort by utilization descending and limit results
        return result.stream()
                .sorted((a, b) -> b.getUtilizationPercentage().compareTo(a.getUtilizationPercentage()))
                .limit(limit)
                .toList();
    }

    /**
     * Get exposure summary aggregated by trading desk.
     * <p>
     * Desk-level exposure is a key metric for managing concentration risk.
     * If one desk has disproportionate exposure, it may indicate a systemic risk.
     */
    @Override
    public List<ExposureSummaryResponse> getExposureSummaryByDesk() {
        List<Object[]> deskExposures = positionRepository.getExposureByDesk();
        List<ExposureSummaryResponse> result = new ArrayList<>();

        for (Object[] row : deskExposures) {
            result.add(ExposureSummaryResponse.builder()
                    .category(String.valueOf(row[0]))
                    .totalExposure(toBigDecimal(row[1]))
                    .totalPnl(toBigDecimal(row[2]))
                    .traderCount(toLong(row[3]))
                    .build());
        }

        return result;
    }

    /**
     * Get PnL summary ranked by trader performance.
     * <p>
     * This report shows who's making and losing money — critical for:
     * - Performance-based compensation (bonuses)
     * - Identifying struggling traders who may need supervision
     * - Attributing PnL to specific desks and strategies
     */
    @Override
    public List<PnLSummaryResponse> getPnLSummary() {
        List<Object[]> pnlRanking = pnlHistoryRepository.getPnLRankingByTrader();
        List<PnLSummaryResponse> result = new ArrayList<>();

        for (Object[] row : pnlRanking) {
            result.add(PnLSummaryResponse.builder()
                    .traderName(String.valueOf(row[0]))
                    .desk(String.valueOf(row[1]))
                    .totalPnl(toBigDecimal(row[2]))
                    .averageDailyPnl(toBigDecimal(row[3]))
                    .worstDay(toBigDecimal(row[4]))
                    .bestDay(toBigDecimal(row[5]))
                    .rank(toInt(row[6]))
                    .build());
        }

        return result;
    }

    // ---- Helper conversion methods for native query results ----

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        return new BigDecimal(value.toString());
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long l) return l;
        return Long.parseLong(value.toString());
    }

    private Integer toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Integer i) return i;
        if (value instanceof Long l) return l.intValue();
        return Integer.parseInt(value.toString());
    }
}
