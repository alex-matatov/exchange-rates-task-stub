package com.shipmonk.testingday.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Collection;
import java.util.TreeSet;

@Configuration
@EnableCaching
public class AppConfiguration {
    public static final String EXCHANGE_RATES_CACHE_NAME = "exchangeRatesCache";

    @Bean
    public RestClient restClient() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(20));
        factory.setConnectTimeout(Duration.ofSeconds(20));

        return RestClient.builder().requestFactory(factory).build();
    }

    @Bean
    public CaffeineCache exchangeRatesCache() {
        return new CaffeineCache(EXCHANGE_RATES_CACHE_NAME,
            Caffeine.newBuilder()
                .initialCapacity(30)
                .maximumSize(180)
                .build());
    }

    @Bean
    public KeyGenerator paramsCacheKeyGenerator() {
        return (target, method, params) -> {
            if (params.length == 1 && params[0] instanceof Collection) {
                return new TreeSet<>(((Collection<?>) params[0])).toString();
            }
            //default
            return StringUtils.arrayToDelimitedString(params, "_");
        };
    }

}
