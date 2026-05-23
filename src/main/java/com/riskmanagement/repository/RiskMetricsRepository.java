package com.riskmanagement.repository;

import com.riskmanagement.entity.RiskMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiskMetricsRepository extends JpaRepository<RiskMetrics, UUID> {

    Optional<RiskMetrics> findByTraderId(UUID traderId);

    List<RiskMetrics> findByBreachStatusTrue();

    /**
     * Find all traders in breach, ordered by how much they exceed their limit.
     * The ratio (exposure / risk_limit) shows severity — higher ratio = worse breach.
     */
    @Query(value = """
            SELECT rm.*, t.name, t.desk, t.risk_limit
            FROM risk_metrics rm
            INNER JOIN traders t ON rm.trader_id = t.id
            WHERE rm.breach_status = true
            ORDER BY (rm.total_exposure / t.risk_limit) DESC
            """, nativeQuery = true)
    List<Object[]> findBreachesWithTraderDetails();
}
