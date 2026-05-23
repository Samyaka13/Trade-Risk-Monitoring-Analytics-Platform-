package com.riskmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopRiskyTraderResponse {

    private UUID traderId;
    private String name;
    private String desk;
    private BigDecimal totalExposure;
    private BigDecimal riskLimit;
    private BigDecimal utilizationPercentage;
    private BigDecimal totalPnl;
    private BigDecimal varEstimate;
    private Boolean breachStatus;
}
