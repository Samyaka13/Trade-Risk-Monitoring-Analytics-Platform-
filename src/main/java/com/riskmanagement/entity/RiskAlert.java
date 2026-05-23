package com.riskmanagement.entity;

import com.riskmanagement.entity.enums.AlertType;
import com.riskmanagement.entity.enums.Severity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Risk alert generated when a trader's activity triggers a risk threshold.
 * <p>
 * In investment banking, the risk management team monitors alerts generated
 * by automated surveillance systems. Critical alerts may require:
 * - Immediate communication to the trader and desk head
 * - Position reduction or hedging
 * - Escalation to senior management or the CRO (Chief Risk Officer)
 * - In extreme cases, automatic trading restrictions
 */
@Entity
@Table(name = "risk_alerts", indexes = {
        @Index(name = "idx_alert_trader_id", columnList = "trader_id"),
        @Index(name = "idx_alert_severity", columnList = "severity"),
        @Index(name = "idx_alert_acknowledged", columnList = "acknowledged"),
        @Index(name = "idx_alert_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskAlert extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trader_id", nullable = false)
    private Trader trader;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 30)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity;

    @Column(nullable = false, length = 500)
    private String message;

    /**
     * Whether the alert has been reviewed and acknowledged by the risk team.
     * Unacknowledged alerts appear on active dashboards.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean acknowledged = false;
}
