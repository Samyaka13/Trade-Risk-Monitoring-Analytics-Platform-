package com.riskmanagement.dto.response;

import com.riskmanagement.entity.enums.InstrumentType;
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
public class PositionResponse {

    private UUID id;
    private UUID traderId;
    private String traderName;
    private String assetSymbol;
    private InstrumentType instrumentType;
    private BigDecimal netQuantity;
    private BigDecimal averagePrice;
    private BigDecimal currentPrice;
    private BigDecimal marketValue;
    private BigDecimal unrealizedPnl;
    private LocalDateTime lastUpdated;

    /** "LONG" if netQuantity > 0, "SHORT" if < 0, "FLAT" if 0 */
    private String direction;
}
