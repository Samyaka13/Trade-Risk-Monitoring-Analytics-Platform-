package com.riskmanagement.entity.enums;

/**
 * Trading desk classification.
 * <p>
 * In investment banks, traders are organized into desks based on the asset class
 * they trade. Each desk operates as a semi-independent profit center with its own
 * risk limits and P&L tracking.
 */
public enum Desk {

    /** Equities desk — trades stocks, ETFs, equity derivatives */
    EQUITY,

    /** Fixed Income desk — trades bonds, interest rate products */
    FIXED_INCOME,

    /** Derivatives desk — trades options, swaps, structured products */
    DERIVATIVES,

    /** Foreign Exchange desk — trades currency pairs, FX forwards */
    FX,

    /** Commodities desk — trades oil, gold, agricultural products */
    COMMODITIES
}
