package com.riskmanagement.riskengine;

import com.riskmanagement.dto.response.RiskMetricsResponse;
import com.riskmanagement.entity.RiskAlert;
import com.riskmanagement.entity.RiskMetrics;
import com.riskmanagement.entity.Trader;
import com.riskmanagement.entity.enums.AlertType;
import com.riskmanagement.entity.enums.Severity;
import com.riskmanagement.exception.ResourceNotFoundException;
import com.riskmanagement.repository.*;
import com.riskmanagement.util.FinancialCalculations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Risk Engine Implementation — the analytical core of the platform.
 * <p>
 * This module implements the key risk calculations that investment banks
 * rely on for real-time risk monitoring:
 * <p>
 * 1. Exposure Calculation — total capital at risk
 * 2. PnL Calculation — unrealized + realized profit/loss
 * 3. VaR Estimation — maximum expected loss at given confidence
 * 4. Breach Detection — comparing exposure against risk limits
 * 5. Alert Generation — automated risk alerts for the risk team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RiskEngineServiceImpl implements RiskEngineService {

    private final TradeRepository tradeRepository;
    private final TraderRepository traderRepository;
    private final RiskMetricsRepository riskMetricsRepository;
    private final RiskAlertRepository riskAlertRepository;
    private final PositionRepository positionRepository;

    /**
     * VaR confidence level Z-score.
     * 95% confidence → Z = 1.645
     * 99% confidence → Z = 2.326
     */
    @Value("${risk.engine.var-confidence-level:0.95}")
    private double varConfidenceLevel;

    @Value("${risk.engine.var-holding-period-days:1}")
    private int varHoldingPeriod;

    /**
     * Default volatility assumption when historical data is insufficient.
     * 2% daily volatility is a reasonable assumption for diversified equity portfolios.
     */
    @Value("${risk.engine.default-volatility:0.02}")
    private double defaultVolatility;

    /**
     * Recalculate all risk metrics for a specific trader.
     * <p>
     * This is the main entry point called after:
     * - A new trade is booked
     * - A trade is closed or modified
     * - Market data is updated (mark-to-market refresh)
     */
    @Override
    public void recalculateRiskMetrics(UUID traderId) {
        log.debug("Recalculating risk metrics for trader: {}", traderId);

        Trader trader = traderRepository.findById(traderId)
                .orElseThrow(() -> new ResourceNotFoundException("Trader", "id", traderId));

        // 1. Calculate total exposure
        BigDecimal totalExposure = tradeRepository.calculateTotalExposure(traderId);

        // 2. Calculate total PnL (unrealized + realized)
        BigDecimal unrealizedPnl = tradeRepository.calculateUnrealizedPnL(traderId);
        BigDecimal realizedPnl = tradeRepository.calculateRealizedPnL(traderId);
        BigDecimal totalPnl = unrealizedPnl.add(realizedPnl);

        // 3. Calculate VaR
        BigDecimal varEstimate = calculateVaR(totalExposure);

        // 4. Detect breaches
        boolean breachStatus = totalExposure.compareTo(trader.getRiskLimit()) > 0;

        // 5. Upsert risk metrics
        RiskMetrics metrics = riskMetricsRepository.findByTraderId(traderId)
                .orElse(RiskMetrics.builder()
                        .trader(trader)
                        .totalExposure(BigDecimal.ZERO)
                        .totalPnl(BigDecimal.ZERO)
                        .varEstimate(BigDecimal.ZERO)
                        .breachStatus(false)
                        .build());

        boolean wasInBreach = metrics.getBreachStatus();

        metrics.setTotalExposure(totalExposure);
        metrics.setTotalPnl(totalPnl);
        metrics.setVarEstimate(varEstimate);
        metrics.setBreachStatus(breachStatus);
        metrics.setLastCalculated(LocalDateTime.now());

        riskMetricsRepository.save(metrics);

        // 6. Generate alerts if thresholds breached
        generateAlertsIfNeeded(trader, totalExposure, varEstimate, wasInBreach, breachStatus);

        log.info("Risk metrics updated for {}: exposure={}, PnL={}, VaR={}, breach={}",
                trader.getName(), totalExposure, totalPnl, varEstimate, breachStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public RiskMetricsResponse getRiskMetrics(UUID traderId) {
        Trader trader = traderRepository.findById(traderId)
                .orElseThrow(() -> new ResourceNotFoundException("Trader", "id", traderId));

        RiskMetrics metrics = riskMetricsRepository.findByTraderId(traderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "RiskMetrics", "traderId", traderId));

        BigDecimal unrealizedPnl = tradeRepository.calculateUnrealizedPnL(traderId);
        BigDecimal realizedPnl = tradeRepository.calculateRealizedPnL(traderId);

        return RiskMetricsResponse.builder()
                .traderId(traderId)
                .traderName(trader.getName())
                .desk(trader.getDesk().name())
                .totalExposure(metrics.getTotalExposure())
                .riskLimit(trader.getRiskLimit())
                .totalPnl(metrics.getTotalPnl())
                .unrealizedPnl(unrealizedPnl)
                .realizedPnl(realizedPnl)
                .varEstimate(metrics.getVarEstimate())
                .breachStatus(metrics.getBreachStatus())
                .lastCalculated(metrics.getLastCalculated())
                .utilizationPercentage(FinancialCalculations.calculateUtilizationPercentage(
                        metrics.getTotalExposure(), trader.getRiskLimit()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskMetricsResponse> getBreaches() {
        List<RiskMetrics> breaches = riskMetricsRepository.findByBreachStatusTrue();
        return breaches.stream()
                .map(m -> {
                    Trader trader = m.getTrader();
                    return RiskMetricsResponse.builder()
                            .traderId(trader.getId())
                            .traderName(trader.getName())
                            .desk(trader.getDesk().name())
                            .totalExposure(m.getTotalExposure())
                            .riskLimit(trader.getRiskLimit())
                            .totalPnl(m.getTotalPnl())
                            .varEstimate(m.getVarEstimate())
                            .breachStatus(true)
                            .lastCalculated(m.getLastCalculated())
                            .utilizationPercentage(FinancialCalculations.calculateUtilizationPercentage(
                                    m.getTotalExposure(), trader.getRiskLimit()))
                            .build();
                })
                .toList();
    }

    @Override
    public void recalculateAllTraders() {
        log.info("Recalculating risk metrics for all active traders");
        List<Trader> activeTraders = traderRepository.findByActiveTrue();
        activeTraders.forEach(trader -> {
            try {
                recalculateRiskMetrics(trader.getId());
            } catch (Exception e) {
                log.error("Failed to recalculate risk for trader {}: {}", trader.getName(), e.getMessage());
            }
        });
        log.info("Risk recalculation complete for {} traders", activeTraders.size());
    }

    // ==================== Private Methods ====================

    /**
     * Calculate Value at Risk using the parametric (variance-covariance) method.
     * <p>
     * VaR = Portfolio Value × σ × Z × √t
     * <p>
     * Where:
     * - σ = daily volatility (defaulting to 2% if historical data unavailable)
     * - Z = Z-score for 95% confidence = 1.645
     * - t = holding period in days (typically 1-day for trading book)
     * <p>
     * Note: This is a simplified parametric VaR. Production systems typically use:
     * - Historical VaR: based on actual historical P&L distribution
     * - Monte Carlo VaR: based on thousands of simulated scenarios
     * - Stressed VaR: using crisis-period data (required by Basel III)
     */
    private BigDecimal calculateVaR(BigDecimal portfolioValue) {
        double zScore = varConfidenceLevel >= 0.99 ? 2.326 : 1.645;
        return FinancialCalculations.calculateParametricVaR(
                portfolioValue, defaultVolatility, zScore, varHoldingPeriod);
    }

    /**
     * Generate risk alerts based on threshold analysis.
     * <p>
     * Alert logic:
     * - If trader transitions from non-breach to breach → CRITICAL LIMIT_BREACH alert
     * - If exposure > 80% of limit → HIGH LARGE_EXPOSURE warning
     * - If VaR exceeds a threshold → VAR_BREACH alert
     */
    private void generateAlertsIfNeeded(Trader trader, BigDecimal totalExposure,
                                         BigDecimal varEstimate,
                                         boolean wasInBreach, boolean isInBreach) {
        BigDecimal riskLimit = trader.getRiskLimit();

        // Limit breach — new breach detected
        if (isInBreach && !wasInBreach) {
            BigDecimal utilization = FinancialCalculations.calculateUtilizationPercentage(
                    totalExposure, riskLimit);

            RiskAlert alert = RiskAlert.builder()
                    .trader(trader)
                    .alertType(AlertType.LIMIT_BREACH)
                    .severity(Severity.CRITICAL)
                    .message(String.format(
                            "RISK LIMIT BREACH: %s has exceeded their risk limit. " +
                            "Exposure: $%s / Limit: $%s (%.1f%% utilization). " +
                            "Immediate action required.",
                            trader.getName(), totalExposure, riskLimit, utilization))
                    .build();

            riskAlertRepository.save(alert);
            log.error("🚨 CRITICAL: Risk limit breach for {} — Exposure: {} / Limit: {}",
                    trader.getName(), totalExposure, riskLimit);
        }

        // Early warning — approaching limit (80%+)
        BigDecimal warningThreshold = riskLimit.multiply(BigDecimal.valueOf(0.80));
        if (!isInBreach && totalExposure.compareTo(warningThreshold) > 0) {
            BigDecimal utilization = FinancialCalculations.calculateUtilizationPercentage(
                    totalExposure, riskLimit);

            RiskAlert alert = RiskAlert.builder()
                    .trader(trader)
                    .alertType(AlertType.LARGE_EXPOSURE)
                    .severity(Severity.HIGH)
                    .message(String.format(
                            "EXPOSURE WARNING: %s is approaching their risk limit. " +
                            "Exposure: $%s / Limit: $%s (%.1f%% utilization).",
                            trader.getName(), totalExposure, riskLimit, utilization))
                    .build();

            riskAlertRepository.save(alert);
            log.warn("⚠️ Exposure warning for {} — {}% utilization",
                    trader.getName(), utilization);
        }

        // VaR breach — VaR exceeds 5% of portfolio value
        BigDecimal varThreshold = totalExposure.multiply(BigDecimal.valueOf(0.05));
        if (varEstimate.compareTo(varThreshold) > 0 && totalExposure.compareTo(BigDecimal.ZERO) > 0) {
            RiskAlert alert = RiskAlert.builder()
                    .trader(trader)
                    .alertType(AlertType.VAR_BREACH)
                    .severity(Severity.HIGH)
                    .message(String.format(
                            "VAR ALERT: %s's VaR estimate ($%s) exceeds threshold. " +
                            "Total exposure: $%s. Review portfolio risk.",
                            trader.getName(), varEstimate, totalExposure))
                    .build();

            riskAlertRepository.save(alert);
            log.warn("⚠️ VaR breach for {} — VaR: {} / Threshold: {}",
                    trader.getName(), varEstimate, varThreshold);
        }
    }
}
