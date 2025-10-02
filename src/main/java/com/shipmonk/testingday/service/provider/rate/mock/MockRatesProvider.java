package com.shipmonk.testingday.service.provider.rate.mock;


import com.shipmonk.testingday.service.provider.rate.ExchangeRatesProvider;
import com.shipmonk.testingday.service.provider.rate.RatesInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Service
@ConditionalOnBooleanProperty(name = "app.rate.fixer.mock", havingValue = true, matchIfMissing = false)
public class MockRatesProvider implements ExchangeRatesProvider {

    @Override
    public RatesInfo getRates(String baseCurrencyCode, LocalDate date, Set<String> currencyCodes) {
        return RatesInfo.builder()
            .baseCurrencyCode(baseCurrencyCode)
            .snapshotDate(date)
            .ratesByCurrencyCode(Map.of(
                "EUR", BigDecimal.valueOf(1.1),
                "USD", BigDecimal.valueOf(1.0),
                "GBP", BigDecimal.valueOf(0.9),
                "PLN", BigDecimal.valueOf(4.5),
                "CAD", BigDecimal.valueOf(1.3),
                "CZK", BigDecimal.valueOf(23.0)
            ))
            .build();
    }
}
