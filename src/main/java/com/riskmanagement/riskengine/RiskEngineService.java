package com.riskmanagement.riskengine;

import com.riskmanagement.dto.response.RiskMetricsResponse;

import java.util.List;
import java.util.UUID;

/**
 * Risk Engine — the core analytical module of the platform.
 * <p>
 * In investment banking, the risk engine is responsible for:
 * - Real-time calculation of risk metrics (exposure, VaR, PnL)
 * - Monitoring trader risk limits
 * - Detecting breaches and generating alerts
 * - Providing data for regulatory reporting (Basel III/IV)
 * <p>
 * The risk engine typically runs as a near-real-time system, recalculating
 * metrics on every trade event and market data update.
 */
public interface RiskEngineService {

    /**
     * Recalculate all risk metrics for a trader.
     * Called after every trade event or market data update.
     */
    void recalculateRiskMetrics(UUID traderId);

    /**
     * Get current risk metrics for a trader.
     */
    RiskMetricsResponse getRiskMetrics(UUID traderId);

    /**
     * Get all traders currently in breach of their risk limits.
     */
    List<RiskMetricsResponse> getBreaches();

    /**
     * Recalculate risk for all active traders.
     * Typically called on market data refresh.
     */
    void recalculateAllTraders();
}
