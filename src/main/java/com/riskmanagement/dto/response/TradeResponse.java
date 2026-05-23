package com.riskmanagement.dto.response;

import com.riskmanagement.entity.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeResponse {

    private UUID id;
    private UUID traderId;
    private String traderName;
    private String assetSymbol;
    private AssetType assetType;
    private TradeType tradeType;
    private InstrumentType instrumentType;
    private BigDecimal quantity;
    private BigDecimal entryPrice;
    private BigDecimal currentPrice;
    private BigDecimal exitPrice;
    private TradeStatus tradeStatus;
    private String counterparty;
    private LocalDateTime tradeDate;
    private LocalDate settlementDate;
    private LocalDateTime closedAt;

    /** Calculated field: (currentPrice - entryPrice) × quantity for BUY trades */
    private BigDecimal unrealizedPnl;
    /** Calculated field: |quantity × currentPrice| */
    private BigDecimal exposure;
}
