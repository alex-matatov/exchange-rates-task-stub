package com.shipmonk.testingday.service;

import com.shipmonk.testingday.config.AppConfiguration;
import com.shipmonk.testingday.dto.CurrencySnapshotDto;
import com.shipmonk.testingday.repository.CurrencySnapshotExtRepository;
import com.shipmonk.testingday.service.provider.rate.ExchangeRatesProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExchangeRatesService {
    public static final String DEFAULT_CURRENCY = "USD";
    private final ExchangeRatesProvider exchangeRatesProvider;
    private final CurrencySnapshotExtRepository currencySnapshotRepository;
    private final CacheManager cacheManager;


    public CurrencySnapshotDto getRates(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot get rates for future date: " + date);
        }

        var snapshot = currencySnapshotRepository.getCurrencySnapshotWithRates(DEFAULT_CURRENCY, date);
        if (!snapshot.isEmpty()) {
            log.debug("Rates Cache hit for {}/{}", DEFAULT_CURRENCY, date);
            return CurrencySnapshotDto.fromEntity(snapshot.getFirst());
        }

        log.debug("Rates Cache miss for {}/{}", DEFAULT_CURRENCY, date);
        cacheManager
            .getCache(AppConfiguration.EXCHANGE_RATES_CACHE_NAME)
            .evict(new SimpleKey(DEFAULT_CURRENCY, date));

        log.info("Fetching rates from Fixer for {}/{}", DEFAULT_CURRENCY, date);
        var rates = exchangeRatesProvider.getRates(DEFAULT_CURRENCY, date);
        var dto = CurrencySnapshotDto.builder()
            .baseCurrencyCode(rates.getBaseCurrencyCode())
            .snapshotDate(rates.getSnapshotDate())
            .ratesByCurrencyCode(rates.getRatesByCurrencyCode())
            .build();

        log.debug("Saving rates to DB for {}/{}", DEFAULT_CURRENCY, date);
        currencySnapshotRepository.save(dto.toEntity());

        return dto;
    }
}
