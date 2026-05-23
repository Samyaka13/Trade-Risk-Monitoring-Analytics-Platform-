package com.riskmanagement.entity.enums;

/**
 * Trader role hierarchy within a trading desk.
 * <p>
 * Role determines default risk limits and authorization levels.
 * Senior traders and desk heads typically have higher risk limits.
 */
public enum TraderRole {

    JUNIOR_TRADER,
    SENIOR_TRADER,
    DESK_HEAD,
    MANAGING_DIRECTOR
}
