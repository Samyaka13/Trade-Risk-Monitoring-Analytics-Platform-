package com.riskmanagement.entity.enums;

/**
 * Financial instrument type.
 * <p>
 * STOCK — direct equity ownership, settles T+2.
 * FUTURE — standardized derivative contract obligating the buyer/seller
 *          to transact at a predetermined price on a future date.
 *          Futures involve leverage and margin requirements.
 * OPTION — derivative giving the right (but not obligation) to buy/sell
 *          at a strike price before expiration.
 */
public enum InstrumentType {

    STOCK,
    FUTURE,
    OPTION
}
