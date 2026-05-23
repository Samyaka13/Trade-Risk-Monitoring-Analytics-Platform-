package com.riskmanagement.repository;

import com.riskmanagement.entity.Trader;
import com.riskmanagement.entity.enums.Desk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TraderRepository extends JpaRepository<Trader, UUID> {

    Optional<Trader> findByEmployeeId(String employeeId);

    List<Trader> findByDesk(Desk desk);

    List<Trader> findByActiveTrue();

    /**
     * Find top risky traders — those whose total exposure is closest to or exceeds their risk limit.
     * Uses a subquery to join with risk_metrics and rank by exposure-to-limit ratio.
     */
    @Query(value = """
            SELECT t.* FROM traders t
            INNER JOIN risk_metrics rm ON t.id = rm.trader_id
            WHERE t.active = true
            ORDER BY (rm.total_exposure / t.risk_limit) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Trader> findTopRiskyTraders(@Param("limit") int limit);
}
