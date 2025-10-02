package com.shipmonk.testingday.repository;

import com.shipmonk.testingday.config.AppConfiguration;
import com.shipmonk.testingday.entity.CurrencySnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class CurrencySnapshotExtRepository {
    private final CurrencySnapshotRepository currencySnapshotRepository;

    @Cacheable(AppConfiguration.EXCHANGE_RATES_CACHE_NAME)
    public List<CurrencySnapshot> getCurrencySnapshotWithRates(String baseCurrency, LocalDate date) {
        return currencySnapshotRepository.getCurrencySnapshotWithRates(baseCurrency, date);
    }

    public CurrencySnapshot save(CurrencySnapshot currencySnapshot) {
        return currencySnapshotRepository.save(currencySnapshot);
    }
}
