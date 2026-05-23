package com.riskmanagement.entity.enums;

/**
 * Trade direction.
 * <p>
 * BUY (long) — the trader profits when the asset price increases.
 * SELL (short) — the trader profits when the asset price decreases.
 * <p>
 * In position management, BUY adds to net quantity and SELL subtracts.
 */
public enum TradeType {

    BUY,
    SELL
}
