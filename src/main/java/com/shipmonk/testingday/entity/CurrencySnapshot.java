package com.shipmonk.testingday.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "currency_snapshot")
@Builder
public class CurrencySnapshot extends BaseEntity {
    @Id
    @SequenceGenerator(name = "currency_snapshot_id_seq", sequenceName = "currency_snapshot_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "currency_snapshot_id_seq")
    @Access(AccessType.PROPERTY)
    private Long id;

    @Column(name="snapshot_date")
    private LocalDate snapshotDate;

    @Column(name = "currency_code", length = 3)
    private String baseCurrencyCode;

    @OneToMany(mappedBy = "currencySnapshot", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<CurrencyRate> rates = new ArrayList<>();
}
