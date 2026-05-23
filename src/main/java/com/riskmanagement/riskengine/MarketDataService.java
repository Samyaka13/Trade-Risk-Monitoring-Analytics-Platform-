package com.riskmanagement.riskengine;

import com.riskmanagement.entity.Trade;
import com.riskmanagement.entity.enums.TradeStatus;
import com.riskmanagement.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Market Data Service — simulates real-time market price feeds.
 * <p>
 * In production investment banking systems, market data comes from:
 * - Bloomberg Terminal (Bloomberg B-PIPE)
 * - Reuters/Refinitiv (Elektron)
 * - Direct exchange feeds (NYSE, NASDAQ, CME)
 * - Internal dark pool prices
 * <p>
 * This service simulates price movements using a simple random walk model,
 * which is a reasonable approximation for short-term price dynamics
 * (consistent with the Efficient Market Hypothesis / geometric Brownian motion).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MarketDataService {

    private final TradeRepository tradeRepository;
    private final RiskEngineService riskEngineService;

    private final Random random = new Random();

    /**
     * Base reference prices for common traded instruments.
     * In production, these would come from a real-time market data feed.
     */
    private final Map<String, BigDecimal> basePrices = new ConcurrentHashMap<>(Map.of(
            "AAPL", new BigDecimal("178.50"),
            "MSFT", new BigDecimal("378.25"),
            "GOOGL", new BigDecimal("141.80"),
            "TSLA", new BigDecimal("248.50"),
            "JPM", new BigDecimal("196.40"),
            "GS", new BigDecimal("458.75"),
            "AMZN", new BigDecimal("185.60"),
            "NVDA", new BigDecimal("875.30"),
            "META", new BigDecimal("505.75"),
            "BAC", new BigDecimal("35.20")
    ));

    /**
     * Simulates periodic market data updates.
     * <p>
     * Runs every 60 seconds (configurable). In real systems, market data
     * updates happen hundreds of times per second per instrument.
     * <p>
     * The price simulation uses a random walk:
     * New Price = Old Price × (1 + ε)
     * where ε ~ Normal(0, σ) with σ = daily volatility / √(trading minutes)
     */
    @Scheduled(fixedDelayString = "${risk.engine.market-data-refresh-interval-ms:60000}")
    @Transactional
    public void updateMarketPrices() {
        log.debug("Refreshing market prices...");

        List<Trade> openTrades = tradeRepository.findByTradeStatus(TradeStatus.OPEN);
        if (openTrades.isEmpty()) return;

        for (Trade trade : openTrades) {
            BigDecimal newPrice = simulatePriceMovement(trade.getCurrentPrice());
            trade.setCurrentPrice(newPrice);
        }

        tradeRepository.saveAll(openTrades);

        // Trigger risk recalculation for all affected traders
        openTrades.stream()
                .map(trade -> trade.getTrader().getId())
                .distinct()
                .forEach(traderId -> {
                    try {
                        riskEngineService.recalculateRiskMetrics(traderId);
                    } catch (Exception e) {
                        log.error("Risk recalc failed for trader {}: {}", traderId, e.getMessage());
                    }
                });

        log.debug("Market prices updated for {} open trades", openTrades.size());
    }

    /**
     * Get the current simulated price for an asset.
     */
    public BigDecimal getCurrentPrice(String assetSymbol) {
        return basePrices.getOrDefault(assetSymbol.toUpperCase(),
                new BigDecimal("100.00"));
    }

    /**
     * Simulate price movement using geometric Brownian motion (GBM).
     * <p>
     * GBM is the standard model for stock price dynamics in quantitative finance
     * (used in Black-Scholes option pricing model).
     * <p>
     * ΔS/S = μΔt + σε√Δt
     * <p>
     * Simplified here to: S_new = S_old × (1 + σ × ε)
     * where ε ~ N(0, 1) and σ = 0.5% per update (intraday volatility)
     */
    private BigDecimal simulatePriceMovement(BigDecimal currentPrice) {
        // Intraday volatility: ~0.5% per update period
        double volatility = 0.005;
        double change = random.nextGaussian() * volatility;

        // Clamp extreme moves (circuit breaker simulation — prevent >5% moves)
        change = Math.max(-0.05, Math.min(0.05, change));

        BigDecimal multiplier = BigDecimal.ONE.add(BigDecimal.valueOf(change));
        return currentPrice.multiply(multiplier).setScale(4, RoundingMode.HALF_UP);
    }
}
