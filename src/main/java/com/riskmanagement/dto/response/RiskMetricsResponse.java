package com.riskmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskMetricsResponse {

    private UUID traderId;
    private String traderName;
    private String desk;
    private BigDecimal totalExposure;
    private BigDecimal riskLimit;
    private BigDecimal totalPnl;
    private BigDecimal unrealizedPnl;
    private BigDecimal realizedPnl;
    private BigDecimal varEstimate;
    private Boolean breachStatus;
    private LocalDateTime lastCalculated;

    /** Exposure as a percentage of risk limit */
    private BigDecimal utilizationPercentage;
}
