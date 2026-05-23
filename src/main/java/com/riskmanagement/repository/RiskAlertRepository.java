package com.riskmanagement.repository;

import com.riskmanagement.entity.RiskAlert;
import com.riskmanagement.entity.enums.Severity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RiskAlertRepository extends JpaRepository<RiskAlert, UUID> {

    List<RiskAlert> findByTraderId(UUID traderId);

    List<RiskAlert> findBySeverity(Severity severity);

    List<RiskAlert> findByAcknowledgedFalse();

    Page<RiskAlert> findAll(Pageable pageable);

    /**
     * Find critical unacknowledged alerts — highest priority for the risk team.
     */
    @Query("""
            SELECT ra FROM RiskAlert ra
            JOIN FETCH ra.trader
            WHERE ra.severity = 'CRITICAL'
            AND ra.acknowledged = false
            ORDER BY ra.createdAt DESC
            """)
    List<RiskAlert> findCriticalActiveAlerts();

    /**
     * Count active alerts by severity — for dashboard widgets.
     */
    @Query(value = """
            SELECT severity, COUNT(*) as alert_count
            FROM risk_alerts
            WHERE acknowledged = false
            GROUP BY severity
            ORDER BY
                CASE severity
                    WHEN 'CRITICAL' THEN 1
                    WHEN 'HIGH' THEN 2
                    WHEN 'MEDIUM' THEN 3
                    WHEN 'LOW' THEN 4
                END
            """, nativeQuery = true)
    List<Object[]> countActiveAlertsBySeverity();
}
