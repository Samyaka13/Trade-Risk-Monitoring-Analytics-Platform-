package com.riskmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Daily PnL snapshot for a trader.
 * <p>
 * PnL (Profit and Loss) history is essential for:
 * - Performance attribution and trader evaluation
 * - Risk analytics (VaR backtesting)
 * - Regulatory reporting (daily PnL explain)
 * - Detecting unusual PnL swings that may indicate rogue trading
 * <p>
 * In real banks, daily PnL is computed at end-of-day (EOD) and includes:
 * - Realized PnL from closed trades
 * - Unrealized PnL changes (MTM movements)
 * - Funding costs and commissions (simplified out here)
 */
@Entity
@Table(name = "pnl_history",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pnl_trader_date",
                columnNames = {"trader_id", "date"}
        ),
        indexes = {
                @Index(name = "idx_pnl_trader_date", columnList = "trader_id, date")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PnLHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trader_id", nullable = false)
    private Trader trader;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "daily_pnl", nullable = false, precision = 19, scale = 4)
    private BigDecimal dailyPnl;

    @Column(name = "cumulative_pnl", nullable = false, precision = 19, scale = 4)
    private BigDecimal cumulativePnl;
}
