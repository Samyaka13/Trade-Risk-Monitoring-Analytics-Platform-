package com.riskmanagement.entity.enums;

/**
 * Application user roles for RBAC (Role-Based Access Control).
 * <p>
 * In banking systems, different roles have different levels of access:
 * - ADMIN: Full system access, user management
 * - TRADER: Can create/modify trades, view own positions and risk
 * - RISK_MANAGER: Read access to all trades, positions, risk metrics, and alerts
 */
public enum UserRole {

    ROLE_ADMIN,
    ROLE_TRADER,
    ROLE_RISK_MANAGER
}
