package com.shipmonk.testingday.service.provider.rate.fixer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "app.rate.fixer")
public record FixerConfig(String url, String accessKey) {
    @ConstructorBinding
    public FixerConfig {
    }
}
