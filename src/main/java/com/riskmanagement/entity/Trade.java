package com.riskmanagement.entity;

import com.riskmanagement.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a single trade execution.
 * <p>
 * A trade is the fundamental unit of activity in capital markets. When a trader
 * buys or sells a financial instrument, a trade record is created capturing all
 * the economics of the transaction.
 * <p>
 * Key investment banking concepts:
 * <ul>
 *   <li><b>Entry Price</b> — the price at which the trade was executed</li>
 *   <li><b>Current Price</b> — latest market price, used for mark-to-market valuation</li>
 *   <li><b>Counterparty</b> — the other party in the trade (e.g., another bank, broker, exchange)</li>
 *   <li><b>Settlement Date</b> — when the trade actually settles (cash and securities exchanged).
 *       Typically T+2 for equities, T+1 for government bonds.</li>
 * </ul>
 */
@Entity
@Table(name = "trades", indexes = {
        @Index(name = "idx_trade_trader_id", columnList = "trader_id"),
        @Index(name = "idx_trade_asset_symbol", columnList = "asset_symbol"),
        @Index(name = "idx_trade_status", columnList = "trade_status"),
        @Index(name = "idx_trade_date", columnList = "trade_date"),
        @Index(name = "idx_trade_counterparty", columnList = "counterparty")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trade extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trader_id", nullable = false)
    private Trader trader;

    @Column(name = "asset_symbol", nullable = false, length = 20)
    private String assetSymbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false, length = 20)
    private AssetType assetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type", nullable = false, length = 10)
    private TradeType tradeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "instrument_type", nullable = false, length = 20)
    private InstrumentType instrumentType;

    /**
     * Number of units traded. For futures, this represents the number of contracts.
     * Always positive — direction is determined by {@link #tradeType}.
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    /**
     * The price at which the trade was executed.
     * This is the fill price and does not change after trade creation.
     */
    @Column(name = "entry_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal entryPrice;

    /**
     * Current market price of the asset.
     * Updated periodically via the market data service for mark-to-market (MTM) valuation.
     * <p>
     * Mark-to-Market: The practice of valuing a position at the current market price
     * rather than the purchase price. This gives a realistic view of the portfolio's
     * current value and is required by accounting standards (IFRS/US GAAP).
     */
    @Column(name = "current_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal currentPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_status", nullable = false, length = 20)
    @Builder.Default
    private TradeStatus tradeStatus = TradeStatus.OPEN;

    /**
     * The counterparty to this trade.
     * <p>
     * In OTC (Over-The-Counter) markets, the counterparty is the other institution
     * on the opposite side of the trade. Counterparty risk — the risk that the other
     * party defaults — is a major concern in investment banking (cf. Lehman Brothers 2008).
     */
    @Column(nullable = false, length = 100)
    private String counterparty;

    @Column(name = "trade_date", nullable = false)
    private LocalDateTime tradeDate;

    /**
     * Settlement date: when cash and securities are actually exchanged.
     * Standard settlement cycles: T+2 for equities, T+1 for government bonds.
     */
    @Column(name = "settlement_date")
    private LocalDate settlementDate;

    /**
     * Exit price when the trade is closed.
     * Used to calculate realized PnL = (exitPrice - entryPrice) × quantity.
     */
    @Column(name = "exit_price", precision = 19, scale = 4)
    private BigDecimal exitPrice;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;
}
