package com.riskmanagement.repository;

import com.riskmanagement.entity.PnLHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PnLHistoryRepository extends JpaRepository<PnLHistory, UUID> {

    List<PnLHistory> findByTraderIdOrderByDateDesc(UUID traderId);

    List<PnLHistory> findByTraderIdAndDateBetween(UUID traderId, LocalDate start, LocalDate end);

    /**
     * PnL summary across all traders — aggregated daily PnL for the entire book.
     * Uses window functions for running totals.
     */
    @Query(value = """
            SELECT ph.date,
                   SUM(ph.daily_pnl) as total_daily_pnl,
                   SUM(SUM(ph.daily_pnl)) OVER (ORDER BY ph.date) as running_total_pnl
            FROM pnl_history ph
            WHERE ph.date BETWEEN :startDate AND :endDate
            GROUP BY ph.date
            ORDER BY ph.date
            """, nativeQuery = true)
    List<Object[]> getBookPnLSummary(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * PnL ranking by trader — who's making/losing the most money.
     */
    @Query(value = """
            SELECT t.name, t.desk, 
                   SUM(ph.daily_pnl) as total_pnl,
                   AVG(ph.daily_pnl) as avg_daily_pnl,
                   MIN(ph.daily_pnl) as worst_day,
                   MAX(ph.daily_pnl) as best_day,
                   RANK() OVER (ORDER BY SUM(ph.daily_pnl) DESC) as pnl_rank
            FROM pnl_history ph
            INNER JOIN traders t ON ph.trader_id = t.id
            GROUP BY t.id, t.name, t.desk
            ORDER BY total_pnl DESC
            """, nativeQuery = true)
    List<Object[]> getPnLRankingByTrader();
}
