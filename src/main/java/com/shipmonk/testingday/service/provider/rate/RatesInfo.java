package com.shipmonk.testingday.service.provider.rate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@AllArgsConstructor
@Getter
@Builder
public class RatesInfo {
    private LocalDate snapshotDate;
    private String baseCurrencyCode;
    // <"EUR", 1.1>
    private Map<String, BigDecimal> ratesByCurrencyCode;
}
