package com.riskmanagement.entity;

import com.riskmanagement.entity.enums.Desk;
import com.riskmanagement.entity.enums.TraderRole;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a trader within the bank's trading division.
 * <p>
 * Each trader belongs to a specific desk (e.g., Equity, Fixed Income) and has
 * an assigned risk limit — the maximum notional exposure they are authorized to carry.
 * <p>
 * In real investment banks:
 * - Risk limits are set by the Chief Risk Officer (CRO) and reviewed periodically.
 * - Exceeding a risk limit triggers alerts and may result in forced position liquidation.
 * - Traders are identified by employee IDs and mapped to their organizational hierarchy.
 */
@Entity
@Table(name = "traders", indexes = {
        @Index(name = "idx_trader_desk", columnList = "desk"),
        @Index(name = "idx_trader_employee_id", columnList = "employee_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trader extends BaseEntity {

    @Column(name = "employee_id", unique = true, nullable = false, length = 20)
    private String employeeId;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Desk desk;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TraderRole role;

    /**
     * Maximum notional exposure this trader is authorized to carry.
     * Expressed in the base currency (USD).
     * <p>
     * Risk Limit is a core control in banking — it prevents any single trader
     * from accumulating excessive risk that could threaten the firm's capital.
     */
    @Column(name = "risk_limit", nullable = false, precision = 19, scale = 4)
    private BigDecimal riskLimit;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    // ---- Relationships ----

    @OneToMany(mappedBy = "trader", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Trade> trades = new ArrayList<>();

    @OneToMany(mappedBy = "trader", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Position> positions = new ArrayList<>();

    @OneToMany(mappedBy = "trader", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RiskAlert> riskAlerts = new ArrayList<>();
}
