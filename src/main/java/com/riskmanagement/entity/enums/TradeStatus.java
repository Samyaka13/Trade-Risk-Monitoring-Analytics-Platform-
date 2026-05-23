package com.riskmanagement.entity.enums;

/**
 * Trade lifecycle status.
 * <p>
 * In investment banking, a trade goes through a defined lifecycle:
 * <pre>
 *   OPEN → PARTIALLY_FILLED → CLOSED
 *                ↓
 *            CANCELLED
 * </pre>
 * <p>
 * Only OPEN and PARTIALLY_FILLED trades contribute to position calculations
 * and risk exposure. CLOSED trades contribute to realized PnL.
 */
public enum TradeStatus {

    /** Trade is active and fully contributing to position */
    OPEN,

    /** Trade is partially executed — common in large institutional orders */
    PARTIALLY_FILLED,

    /** Trade has been closed — realized PnL is locked in */
    CLOSED,

    /** Trade was cancelled before execution completed */
    CANCELLED
}
