package com.riskmanagement.entity;

import com.riskmanagement.entity.enums.InstrumentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents an aggregated position for a trader in a specific asset.
 * <p>
 * A position is the net result of all open trades in a particular instrument.
 * For example, if a trader has:
 *   - BUY 100 AAPL @ $150
 *   - BUY 50 AAPL @ $155
 *   - SELL 30 AAPL @ $160
 * <p>
 * The net position is: Long 120 AAPL with a weighted average entry price.
 * <p>
 * Key concepts:
 * <ul>
 *   <li><b>Net Quantity</b> — positive = long position, negative = short position</li>
 *   <li><b>Average Price</b> — volume-weighted average entry price across all trades</li>
 *   <li><b>Market Value</b> — current price × |net quantity| (absolute exposure)</li>
 *   <li><b>Unrealized PnL</b> — paper profit/loss = (current price - avg price) × net qty</li>
 * </ul>
 */
@Entity
@Table(name = "positions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_position_trader_asset_instrument",
                columnNames = {"trader_id", "asset_symbol", "instrument_type"}
        ),
        indexes = {
                @Index(name = "idx_position_trader_id", columnList = "trader_id"),
                @Index(name = "idx_position_asset", columnList = "asset_symbol")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Position extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trader_id", nullable = false)
    private Trader trader;

    @Column(name = "asset_symbol", nullable = false, length = 20)
    private String assetSymbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "instrument_type", nullable = false, length = 20)
    private InstrumentType instrumentType;

    /**
     * Net quantity across all open trades.
     * Positive = long (trader owns the asset, profits when price rises).
     * Negative = short (trader has sold borrowed assets, profits when price falls).
     */
    @Column(name = "net_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal netQuantity;

    /**
     * Volume-weighted average entry price.
     * Recalculated whenever new trades are added to the position.
     */
    @Column(name = "average_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal averagePrice;

    /**
     * Market Value = |Net Quantity| × Current Market Price.
     * Represents the total notional value of the position at current prices.
     */
    @Column(name = "market_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal marketValue;

    /**
     * Unrealized PnL = (Current Price - Average Price) × Net Quantity.
     * This is the "paper" profit or loss — it hasn't been realized through closing trades.
     * <p>
     * Unrealized PnL fluctuates with market movements and is a key metric
     * on trading floor dashboards and risk reports.
     */
    @Column(name = "unrealized_pnl", nullable = false, precision = 19, scale = 4)
    private BigDecimal unrealizedPnl;

    @Column(name = "current_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal currentPrice;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
