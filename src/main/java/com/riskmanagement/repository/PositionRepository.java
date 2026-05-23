package com.riskmanagement.repository;

import com.riskmanagement.entity.Position;
import com.riskmanagement.entity.enums.InstrumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {

    List<Position> findByTraderId(UUID traderId);

    Optional<Position> findByTraderIdAndAssetSymbolAndInstrumentType(
            UUID traderId, String assetSymbol, InstrumentType instrumentType);

    /**
     * Find the largest positions across all traders by absolute market value.
     * Used in reporting to identify concentration risks at the book level.
     */
    @Query(value = """
            SELECT p.*, t.name as trader_name, t.desk as trader_desk
            FROM positions p
            INNER JOIN traders t ON p.trader_id = t.id
            ORDER BY ABS(p.market_value) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findLargestPositions(@Param("limit") int limit);

    /**
     * Aggregated position exposure by desk — for desk-level risk monitoring.
     */
    @Query(value = """
            SELECT t.desk,
                   SUM(ABS(p.market_value)) as desk_exposure,
                   SUM(p.unrealized_pnl) as desk_pnl,
                   COUNT(DISTINCT p.trader_id) as trader_count
            FROM positions p
            INNER JOIN traders t ON p.trader_id = t.id
            GROUP BY t.desk
            ORDER BY desk_exposure DESC
            """, nativeQuery = true)
    List<Object[]> getExposureByDesk();
}
