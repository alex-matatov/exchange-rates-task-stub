package com.shipmonk.testingday.service;

import com.shipmonk.testingday.dto.CurrencySnapshotDto;
import com.shipmonk.testingday.entity.CurrencySnapshot;
import com.shipmonk.testingday.repository.CurrencySnapshotExtRepository;
import com.shipmonk.testingday.service.provider.rate.ExchangeRatesProvider;
import com.shipmonk.testingday.service.provider.rate.RatesInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ExchangeRatesServiceTest {

    private ExchangeRatesProvider exchangeRatesProvider;
    private CurrencySnapshotExtRepository currencySnapshotRepository;
    private CacheManager cacheManager;
    private ExchangeRatesService service;

    @BeforeEach
    void setUp() {
        exchangeRatesProvider = mock(ExchangeRatesProvider.class);
        currencySnapshotRepository = mock(CurrencySnapshotExtRepository.class);
        cacheManager = mock(CacheManager.class);
        Cache cache = new ConcurrentMapCache("exchangeRates");
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        service = new ExchangeRatesService(exchangeRatesProvider, currencySnapshotRepository, cacheManager);
    }

    @Test
    void testCacheAndRepositoryInteraction() {
        var date = LocalDate.of(2023, 1, 1);
        when(currencySnapshotRepository.getCurrencySnapshotWithRates("USD", date))
            .thenReturn(Collections.emptyList())
            .thenReturn(List.of(CurrencySnapshot.builder()
                .baseCurrencyCode("USD")
                .snapshotDate(date)
                .build()));

        var ratesInfo = RatesInfo.builder()
            .baseCurrencyCode("USD")
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
        var expectedSnapshot = CurrencySnapshotDto.builder()
            .baseCurrencyCode(ratesInfo.getBaseCurrencyCode())
            .snapshotDate(ratesInfo.getSnapshotDate())
            .ratesByCurrencyCode(ratesInfo.getRatesByCurrencyCode())
            .build();

        when(exchangeRatesProvider.getRates("USD", date)).thenReturn(ratesInfo);
        when(currencySnapshotRepository.save(any(CurrencySnapshot.class))).thenReturn(null);

        // First call: cache miss, provider and save should be called
        var rates = service.getRates(date);

        assertEquals(expectedSnapshot, rates);

        verify(currencySnapshotRepository, times(1)).getCurrencySnapshotWithRates("USD", date);
        verify(exchangeRatesProvider, times(1)).getRates("USD", date);
        verify(currencySnapshotRepository, times(1)).save(any(CurrencySnapshot.class));

        // Second call: cache hit, only repository should be called
        CurrencySnapshotDto result2 = service.getRates(date);
        assertEquals(expectedSnapshot, rates);
        verify(currencySnapshotRepository, times(2)).getCurrencySnapshotWithRates("USD", date);
        verify(exchangeRatesProvider, times(1)).getRates("USD", date);
        verify(currencySnapshotRepository, times(1)).save(any(CurrencySnapshot.class));
    }
}
