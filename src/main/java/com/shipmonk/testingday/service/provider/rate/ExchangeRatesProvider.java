package com.shipmonk.testingday.service.provider.rate;

import java.time.LocalDate;
import java.util.Set;

public interface ExchangeRatesProvider {
    default RatesInfo getRates(String baseCurrencyCode, LocalDate date) {
        return getRates(baseCurrencyCode, date, Set.of());
    }
    RatesInfo getRates(String baseCurrencyCode, LocalDate date, Set<String> currencyCodes);
}
