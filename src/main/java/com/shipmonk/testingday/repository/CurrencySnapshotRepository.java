package com.shipmonk.testingday.repository;

import com.shipmonk.testingday.entity.CurrencySnapshot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CurrencySnapshotRepository extends CrudRepository<CurrencySnapshot, Long> {

    @Query("""
             SELECT cr
             FROM CurrencySnapshot cr LEFT JOIN FETCH cr.rates r
             WHERE cr.snapshotDate = :snapshotDate
             AND cr.baseCurrencyCode = :baseCurrencyCode
            """)
    List<CurrencySnapshot> getCurrencySnapshotWithRates(@Param("baseCurrencyCode") String baseCurrency,
                                                        @Param("snapshotDate") LocalDate date);
}
