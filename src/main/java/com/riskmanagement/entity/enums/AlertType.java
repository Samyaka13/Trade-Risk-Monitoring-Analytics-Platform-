package com.riskmanagement.entity.enums;

/**
 * Risk alert classification.
 * <p>
 * Banks have automated surveillance systems that generate alerts when
 * predefined risk thresholds are breached. These alerts are reviewed
 * by the risk management team and may trigger position reduction or
 * trading restrictions.
 */
public enum AlertType {

    /** Trader's total exposure exceeds their assigned risk limit */
    LIMIT_BREACH,

    /** Excessive concentration in a single asset or counterparty */
    CONCENTRATION_RISK,

    /** Single position exceeds a notional threshold */
    LARGE_EXPOSURE,

    /** Value-at-Risk estimate exceeds acceptable threshold */
    VAR_BREACH,

    /** Unusually large PnL swing detected */
    PNL_ANOMALY
}
