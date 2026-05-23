package com.riskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Trade Risk Monitoring & Analytics Platform
 * <p>
 * Enterprise-grade backend system simulating investment banking trade risk management.
 * Inspired by real-world risk platforms used at firms like Nomura, JPMorgan, Goldman Sachs,
 * and Morgan Stanley.
 * <p>
 * Core capabilities:
 * - Trade capture and lifecycle management
 * - Real-time position aggregation
 * - Risk exposure and VaR calculation
 * - PnL monitoring (mark-to-market)
 * - Risk limit breach detection and alerting
 * - Regulatory-style reporting
 */
@SpringBootApplication
@EnableScheduling
public class TradeRiskPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeRiskPlatformApplication.class, args);
    }
}
