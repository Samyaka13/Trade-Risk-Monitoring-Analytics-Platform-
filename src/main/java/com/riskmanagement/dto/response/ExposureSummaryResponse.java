package com.riskmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExposureSummaryResponse {

    private String category;
    private BigDecimal totalExposure;
    private BigDecimal totalPnl;
    private Long traderCount;
    private Long tradeCount;
}
