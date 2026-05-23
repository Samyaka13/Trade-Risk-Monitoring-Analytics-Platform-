package com.riskmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

/**
 * Thrown when a trade would cause the trader to exceed their assigned risk limit.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class RiskLimitExceededException extends RuntimeException {

    private final BigDecimal currentExposure;
    private final BigDecimal riskLimit;
    private final BigDecimal tradeExposure;

    public RiskLimitExceededException(BigDecimal currentExposure, BigDecimal riskLimit, BigDecimal tradeExposure) {
        super(String.format(
                "Risk limit would be exceeded. Current exposure: %s, Trade exposure: %s, Risk limit: %s",
                currentExposure, tradeExposure, riskLimit));
        this.currentExposure = currentExposure;
        this.riskLimit = riskLimit;
        this.tradeExposure = tradeExposure;
    }
}
