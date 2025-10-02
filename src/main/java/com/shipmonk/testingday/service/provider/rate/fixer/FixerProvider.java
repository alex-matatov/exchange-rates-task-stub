package com.shipmonk.testingday.service.provider.rate.fixer;

import com.shipmonk.testingday.service.provider.rate.ExchangeRateProviderException;
import com.shipmonk.testingday.service.provider.rate.ExchangeRatesProvider;
import com.shipmonk.testingday.service.provider.rate.RatesInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ConditionalOnBooleanProperty(name = "app.rate.fixer.mock", havingValue = false, matchIfMissing = true)
@Service
@EnableConfigurationProperties(FixerConfig.class)
@RequiredArgsConstructor
@Slf4j
public class FixerProvider implements ExchangeRatesProvider {
    private static final String DEFAULT_BASE_CURRENCY = "EUR";

    private final RestClient restClient;
    private final FixerConfig config;

    @Getter
    @Setter
    private static class FixerResponse {
        private boolean success;
        private long timestamp;
        private String base;
        private LocalDate date;
        private Map<String, BigDecimal> rates;
    }

    @Override
    public RatesInfo getRates(String baseCurrencyCode, LocalDate date, Set<String> currencyCodes) {
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot get rates for future date: " + date);
        }
        // https://data.fixer.io/api/2013-12-24?access_key=12345&base=EUR&symbols=USD,CAD,EUR
        var url = String.format("%s/api/%d-%02d-%02d?access_key=%s&base=%s",
            config.url(),
            date.getYear(),
            date.getMonthValue(),
            date.getDayOfMonth(),
            config.accessKey(),
            // NOTE: Fixer free plan allows to use only EUR as base currency.
            //       We will convert other currency rates later
            DEFAULT_BASE_CURRENCY);

        if (!currencyCodes.isEmpty()) {
            currencyCodes.add(baseCurrencyCode); // we always need baseCurrencyCode rate to convert other currencies
            url = String.format("%s&symbols=%s", url, String.join(",", currencyCodes));
        }

        try {
            var result = restClient.get().uri(url).retrieve().body(FixerResponse.class);
            return RatesInfo.builder().
                baseCurrencyCode(baseCurrencyCode).
                snapshotDate(result.getDate()).
                ratesByCurrencyCode(recalculateRatesAgainstBaseCurrency(baseCurrencyCode, result.getRates()))
                .build();
        }
        catch (Exception e) {
            log.error("Error fetching rates from Fixer", e);
            throw new ExchangeRateProviderException("Error fetching rates from Fixer", e);
        }
    }

    protected static Map<String, BigDecimal> recalculateRatesAgainstBaseCurrency(String baseCurrencyCode,
                                                                                 Map<String, BigDecimal> ratesByCurrency) {
        var defaultToRequestedBaseCurrencyRate = ratesByCurrency.get(baseCurrencyCode);
        if (defaultToRequestedBaseCurrencyRate == null || BigDecimal.ZERO.equals(defaultToRequestedBaseCurrencyRate)) {
            throw new IllegalStateException("Fixer response does not contain USD rate");
        }

        var multiplier = BigDecimal.ONE.divide(defaultToRequestedBaseCurrencyRate, 6, RoundingMode.HALF_UP);
        return ratesByCurrency.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().multiply(multiplier).setScale(6, RoundingMode.HALF_UP)
            ));
    }
}
