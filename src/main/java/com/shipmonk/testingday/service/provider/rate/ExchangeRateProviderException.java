package com.shipmonk.testingday.service.provider.rate;

public class ExchangeRateProviderException extends RuntimeException {
    public ExchangeRateProviderException(String message) {
        super(message);
    }

    public ExchangeRateProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
