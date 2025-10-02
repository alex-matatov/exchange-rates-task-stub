package com.shipmonk.testingday.service.provider.rate.fixer;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class FixerProviderTest {

    @Test
    void recalculatesRatesCorrectlyWhenBaseCurrencyPresent() {
        var rates = new HashMap<String, BigDecimal>();
        rates.put("EUR", new BigDecimal("1.0"));
        rates.put("USD", new BigDecimal("1.1731"));
        rates.put("GBP", new BigDecimal("0.8707"));
        rates.put("CAD", new BigDecimal("1.6362"));
        rates.put("CZK", new BigDecimal("24.272"));
        rates.put("JPY", new BigDecimal("172.70"));

        var expectedRecalculatedRates = new HashMap<String, BigDecimal>();
        expectedRecalculatedRates.put("EUR", new BigDecimal("0.852442"));
        expectedRecalculatedRates.put("USD", new BigDecimal("1.000000"));
        expectedRecalculatedRates.put("GBP", new BigDecimal("0.742221"));
        expectedRecalculatedRates.put("CAD", new BigDecimal("1.394766"));
        expectedRecalculatedRates.put("CZK", new BigDecimal("20.690472"));
        expectedRecalculatedRates.put("JPY", new BigDecimal("147.216733"));

        var recalculated = FixerProvider.recalculateRatesAgainstBaseCurrency("USD", rates);

        assertEquals(expectedRecalculatedRates, recalculated);
    }

    @Test
    void throwsIfBaseCurrencyZero() {
        var rates = new HashMap<String, BigDecimal>();
        rates.put("EUR", new BigDecimal("1.0"));
        rates.put("USD", BigDecimal.ZERO);
        rates.put("GBP", new BigDecimal("0.8"));

        var ex = assertThrows(IllegalStateException.class, () ->
            FixerProvider.recalculateRatesAgainstBaseCurrency("USD", rates)
        );
        assertTrue(ex.getMessage().contains("Fixer response does not contain"));
    }
}

