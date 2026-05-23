package com.riskmanagement.entity.enums;

/**
 * Alert severity classification following standard risk management tiers.
 * <p>
 * CRITICAL alerts typically require immediate action and may trigger
 * automated position liquidation in production systems.
 */
public enum Severity {

    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
