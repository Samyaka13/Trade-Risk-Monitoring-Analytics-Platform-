package com.riskmanagement.repository;

import com.riskmanagement.entity.Trade;
import com.riskmanagement.entity.enums.TradeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface TradeRepository extends JpaRepository<Trade, UUID> {

    List<Trade> findByTraderIdAndTradeStatus(UUID traderId, TradeStatus status);

    List<Trade> findByTraderId(UUID traderId);

    List<Trade> findByAssetSymbol(String assetSymbol);

    Page<Trade> findAll(Pageable pageable);

    List<Trade> findByTradeStatus(TradeStatus status);

    /**
     * Calculate total exposure for a trader across all open trades.
     * Exposure = SUM(|quantity × current_price|)
     * <p>
     * Absolute value is used because both long and short positions carry risk.
     */
    @Query("""
            SELECT COALESCE(SUM(ABS(t.quantity * t.currentPrice)), 0)
            FROM Trade t
            WHERE t.trader.id = :traderId
            AND t.tradeStatus IN ('OPEN', 'PARTIALLY_FILLED')
            """)
    BigDecimal calculateTotalExposure(@Param("traderId") UUID traderId);

    /**
     * Calculate unrealized PnL for a trader's open positions.
     * For BUY trades: (currentPrice - entryPrice) × quantity
     * For SELL trades: (entryPrice - currentPrice) × quantity
     */
    @Query("""
            SELECT COALESCE(SUM(
                CASE WHEN t.tradeType = 'BUY'
                    THEN (t.currentPrice - t.entryPrice) * t.quantity
                    ELSE (t.entryPrice - t.currentPrice) * t.quantity
                END
            ), 0)
            FROM Trade t
            WHERE t.trader.id = :traderId
            AND t.tradeStatus IN ('OPEN', 'PARTIALLY_FILLED')
            """)
    BigDecimal calculateUnrealizedPnL(@Param("traderId") UUID traderId);

    /**
     * Calculate realized PnL from closed trades.
     * Realized PnL is locked in when a trade is closed.
     */
    @Query("""
            SELECT COALESCE(SUM(
                CASE WHEN t.tradeType = 'BUY'
                    THEN (t.exitPrice - t.entryPrice) * t.quantity
                    ELSE (t.entryPrice - t.exitPrice) * t.quantity
                END
            ), 0)
            FROM Trade t
            WHERE t.trader.id = :traderId
            AND t.tradeStatus = 'CLOSED'
            AND t.exitPrice IS NOT NULL
            """)
    BigDecimal calculateRealizedPnL(@Param("traderId") UUID traderId);

    /**
     * Exposure aggregation by asset symbol — used for concentration risk analysis.
     */
    @Query(value = """
            SELECT t.asset_symbol,
                   SUM(ABS(t.quantity * t.current_price)) as total_exposure,
                   COUNT(*) as trade_count
            FROM trades t
            WHERE t.trade_status IN ('OPEN', 'PARTIALLY_FILLED')
            GROUP BY t.asset_symbol
            ORDER BY total_exposure DESC
            """, nativeQuery = true)
    List<Object[]> getExposureByAsset();

    /**
     * Find open trades for a specific trader and asset — used for position aggregation.
     */
    List<Trade> findByTraderIdAndAssetSymbolAndTradeStatusIn(
            UUID traderId, String assetSymbol, List<TradeStatus> statuses);
}
