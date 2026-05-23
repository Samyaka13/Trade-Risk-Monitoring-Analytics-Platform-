package com.riskmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Aggregated risk metrics for a trader.
 * <p>
 * Risk metrics provide a snapshot of a trader's overall risk profile.
 * In production banking systems, these are calculated in real-time by
 * dedicated risk engines and displayed on risk dashboards.
 * <p>
 * Key metrics:
 * <ul>
 *   <li><b>Total Exposure</b> — sum of absolute market values across all positions.
 *       Measures total capital at risk regardless of direction.</li>
 *   <li><b>Total PnL</b> — combined unrealized + realized PnL.</li>
 *   <li><b>VaR Estimate</b> — Value at Risk: the maximum expected loss over a given
 *       time horizon at a given confidence level (e.g., 95% 1-day VaR means there's
 *       a 5% chance the portfolio could lose more than this amount in one day).</li>
 *   <li><b>Breach Status</b> — whether the trader has exceeded their risk limit.</li>
 * </ul>
 */
@Entity
@Table(name = "risk_metrics", indexes = {
        @Index(name = "idx_risk_metrics_trader", columnList = "trader_id", unique = true),
        @Index(name = "idx_risk_metrics_breach", columnList = "breach_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskMetrics extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trader_id", nullable = false, unique = true)
    private Trader trader;

    @Column(name = "total_exposure", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalExposure;

    @Column(name = "total_pnl", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalPnl;

    /**
     * Value at Risk (VaR) — one of the most important risk measures in banking.
     * <p>
     * VaR answers: "What is the maximum I can expect to lose over a given period,
     * with a given probability?"
     * <p>
     * Example: A 1-day 95% VaR of $1M means there is a 95% probability that
     * the portfolio will not lose more than $1M in a single day.
     * <p>
     * VaR is used by regulators (Basel III/IV) to determine bank capital requirements.
     */
    @Column(name = "var_estimate", nullable = false, precision = 19, scale = 4)
    private BigDecimal varEstimate;

    @Column(name = "breach_status", nullable = false)
    @Builder.Default
    private Boolean breachStatus = false;

    @Column(name = "last_calculated")
    private LocalDateTime lastCalculated;
}
