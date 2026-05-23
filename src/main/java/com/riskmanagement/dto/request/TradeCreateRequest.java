package com.riskmanagement.dto.request;

import com.riskmanagement.entity.enums.AssetType;
import com.riskmanagement.entity.enums.InstrumentType;
import com.riskmanagement.entity.enums.TradeType;
import jakarta.validation.constraints.*;
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
public class TradeCreateRequest {

    @NotNull(message = "Trader ID is required")
    private UUID traderId;

    @NotBlank(message = "Asset symbol is required")
    @Size(max = 20, message = "Asset symbol must not exceed 20 characters")
    private String assetSymbol;

    @NotNull(message = "Asset type is required")
    private AssetType assetType;

    @NotNull(message = "Trade type (BUY/SELL) is required")
    private TradeType tradeType;

    @NotNull(message = "Instrument type is required")
    private InstrumentType instrumentType;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @DecimalMax(value = "10000000", message = "Quantity exceeds maximum allowed")
    private BigDecimal quantity;

    @NotNull(message = "Entry price is required")
    @Positive(message = "Entry price must be positive")
    private BigDecimal entryPrice;

    @NotBlank(message = "Counterparty is required")
    @Size(max = 100, message = "Counterparty name must not exceed 100 characters")
    private String counterparty;
}
