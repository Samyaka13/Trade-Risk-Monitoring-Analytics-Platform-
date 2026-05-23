package com.riskmanagement.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeUpdateRequest {

    @Positive(message = "Current price must be positive")
    private BigDecimal currentPrice;

    @Positive(message = "Quantity must be positive")
    private BigDecimal quantity;

    private String counterparty;
}
