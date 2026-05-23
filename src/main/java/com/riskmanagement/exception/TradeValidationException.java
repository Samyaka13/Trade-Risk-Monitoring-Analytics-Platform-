package com.riskmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TradeValidationException extends RuntimeException {

    public TradeValidationException(String message) {
        super(message);
    }
}
