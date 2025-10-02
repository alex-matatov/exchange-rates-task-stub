package com.shipmonk.testingday.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "currency_rate")
@Builder
public class CurrencyRate extends BaseEntity {
    @Id
    @SequenceGenerator(name = "currency_rate_id_seq", sequenceName = "currency_rate_id_seq", allocationSize = 200)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "currency_rate_id_seq")
    @Access(AccessType.PROPERTY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_snapshot_ref_id", foreignKey = @ForeignKey(name = "FK_CURRENCY_RATE_TO_CURRENCY_SNAPSHOT"))
    private CurrencySnapshot currencySnapshot;

    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "rate", precision = 19, scale = 5, columnDefinition = "DECIMAL(19,5)")
    private BigDecimal rate;
}
