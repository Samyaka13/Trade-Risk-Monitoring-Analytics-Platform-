package com.riskmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PnLSummaryResponse {

    private String traderName;
    private String desk;
    private BigDecimal totalPnl;
    private BigDecimal averageDailyPnl;
    private BigDecimal worstDay;
    private BigDecimal bestDay;
    private Integer rank;
}
