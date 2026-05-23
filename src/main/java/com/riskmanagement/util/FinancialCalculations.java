package com.riskmanagement.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Financial calculation utilities used across the risk engine and services.
 * <p>
 * All calculations use BigDecimal for precision — floating-point arithmetic
 * (double/float) is unacceptable in financial systems due to rounding errors
 * that can compound across millions of transactions.
 */
public final class FinancialCalculations {

    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);
    private static final int SCALE = 4;

    private FinancialCalculations() {
        // Utility class — no instantiation
    }

    /**
     * Calculate exposure (notional value) for a position.
     * <p>
     * Exposure = |Quantity × Current Price|
     * <p>
     * Absolute value is used because both long and short positions carry risk.
     * A trader who is short 1000 shares of AAPL at $175 has $175,000 of exposure —
     * same as a trader who is long 1000 shares.
     */
    public static BigDecimal calculateExposure(BigDecimal quantity, BigDecimal currentPrice) {
        return quantity.multiply(currentPrice, MC).abs();
    }

    /**
     * Calculate unrealized PnL for a BUY (long) position.
     * <p>
     * Unrealized PnL = (Current Price - Entry Price) × Quantity
     * <p>
     * "Unrealized" means the profit/loss exists on paper but hasn't been locked in
     * by closing the trade. Also called "paper profit" or "mark-to-market PnL."
     */
    public static BigDecimal calculateUnrealizedPnlLong(BigDecimal currentPrice, BigDecimal entryPrice,
                                                         BigDecimal quantity) {
        return currentPrice.subtract(entryPrice).multiply(quantity, MC);
    }

    /**
     * Calculate unrealized PnL for a SELL (short) position.
     * <p>
     * For short positions: PnL = (Entry Price - Current Price) × Quantity
     * <p>
     * A short seller profits when prices fall. If you sold at $100 and the current
     * price is $90, your unrealized profit is $10 per share.
     */
    public static BigDecimal calculateUnrealizedPnlShort(BigDecimal currentPrice, BigDecimal entryPrice,
                                                          BigDecimal quantity) {
        return entryPrice.subtract(currentPrice).multiply(quantity, MC);
    }

    /**
     * Calculate market value of a position.
     * <p>
     * Market Value = |Net Quantity| × Current Price
     * <p>
     * This represents the current monetary value of the position at prevailing market prices.
     */
    public static BigDecimal calculateMarketValue(BigDecimal netQuantity, BigDecimal currentPrice) {
        return netQuantity.abs().multiply(currentPrice, MC);
    }

    /**
     * Calculate basic VaR (Value at Risk) using the parametric (variance-covariance) method.
     * <p>
     * VaR = Portfolio Value × σ × Z × √t
     * <p>
     * Where:
     * - Portfolio Value = total market value of positions
     * - σ (sigma) = estimated daily volatility (standard deviation of returns)
     * - Z = Z-score for confidence level (1.645 for 95%, 2.326 for 99%)
     * - t = holding period in days
     * <p>
     * This is a simplified parametric VaR. In production systems, VaR is typically
     * calculated using:
     * - Historical simulation (using actual historical returns)
     * - Monte Carlo simulation (generating thousands of scenarios)
     * - Full revaluation with risk factor sensitivities (Greeks)
     * <p>
     * VaR is mandated by Basel III/IV regulations for bank capital requirements.
     *
     * @param portfolioValue Total market value of all positions
     * @param volatility     Daily volatility estimate (e.g., 0.02 for 2%)
     * @param confidenceZ    Z-score for confidence level (1.645 for 95%)
     * @param holdingPeriod  Number of days (typically 1 or 10)
     * @return Estimated VaR in currency units
     */
    public static BigDecimal calculateParametricVaR(BigDecimal portfolioValue, double volatility,
                                                     double confidenceZ, int holdingPeriod) {
        double sqrtTime = Math.sqrt(holdingPeriod);
        BigDecimal varMultiplier = BigDecimal.valueOf(volatility * confidenceZ * sqrtTime);
        return portfolioValue.multiply(varMultiplier, MC).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Calculate weighted average price when adding a new trade to an existing position.
     * <p>
     * New Average = (Old Avg × Old Qty + New Price × New Qty) / (Old Qty + New Qty)
     * <p>
     * This is used in position management when a trader adds to an existing position.
     */
    public static BigDecimal calculateWeightedAvgPrice(BigDecimal existingAvgPrice, BigDecimal existingQty,
                                                        BigDecimal newPrice, BigDecimal newQty) {
        BigDecimal totalCost = existingAvgPrice.multiply(existingQty, MC)
                .add(newPrice.multiply(newQty, MC));
        BigDecimal totalQty = existingQty.add(newQty);
        if (totalQty.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return totalCost.divide(totalQty, SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Calculate risk limit utilization as a percentage.
     * <p>
     * Utilization% = (Total Exposure / Risk Limit) × 100
     * <p>
     * A utilization above 100% means the trader is in breach of their risk limit.
     * Most banks trigger alerts at 80-90% utilization as an early warning.
     */
    public static BigDecimal calculateUtilizationPercentage(BigDecimal totalExposure, BigDecimal riskLimit) {
        if (riskLimit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }
        return totalExposure.divide(riskLimit, SCALE, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
