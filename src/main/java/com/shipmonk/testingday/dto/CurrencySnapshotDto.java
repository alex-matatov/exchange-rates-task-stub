package com.shipmonk.testingday.dto;

import com.shipmonk.testingday.entity.CurrencyRate;
import com.shipmonk.testingday.entity.CurrencySnapshot;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
public class CurrencySnapshotDto {
    private LocalDate snapshotDate;
    private String baseCurrencyCode;

    @Setter
    private Map<String, BigDecimal> ratesByCurrencyCode; // <"EUR", 1.1>

    public static CurrencySnapshotDto fromEntity(CurrencySnapshot snapshotEntity) {
        var dto = CurrencySnapshotDto.builder()
            .baseCurrencyCode(snapshotEntity.getBaseCurrencyCode())
            .snapshotDate(snapshotEntity.getSnapshotDate())
            .build();

        if (snapshotEntity.getRates() != null) {
            dto.setRatesByCurrencyCode(snapshotEntity.getRates().stream()
                .collect(Collectors.toMap(CurrencyRate::getCurrencyCode, CurrencyRate::getRate)));
        }
        return dto;


    }

    public CurrencySnapshot toEntity() {
        var entity = CurrencySnapshot.builder().
            baseCurrencyCode(baseCurrencyCode).
            snapshotDate(snapshotDate)
            .build();

        if (ratesByCurrencyCode != null) {
            entity.setRates(ratesByCurrencyCode.entrySet().stream()
                .map(e -> CurrencyRate.builder()
                    .currencySnapshot(entity)
                    .currencyCode(e.getKey())
                    .rate(e.getValue())
                    .build())
                .collect(Collectors.toList()));
        }
        return entity;
    }
}
