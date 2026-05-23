package com.riskmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PnLHistoryResponse {

    private UUID traderId;
    private String traderName;
    private LocalDate date;
    private BigDecimal dailyPnl;
    private BigDecimal cumulativePnl;
}
