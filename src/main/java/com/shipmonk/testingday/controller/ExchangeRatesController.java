package com.shipmonk.testingday.controller;

import com.shipmonk.testingday.dto.ErrorDto;
import com.shipmonk.testingday.service.ExchangeRatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping(path = "/api/v1/rates")
@RequiredArgsConstructor
public class ExchangeRatesController {
    private final ExchangeRatesService exchangeRatesService;

    @RequestMapping(method = RequestMethod.GET, path = "/{day}", produces = "application/json")
    public ResponseEntity<?> getRates(@PathVariable("day") String day) {
        LocalDate snapshotDate;
        try {
            snapshotDate = LocalDate.parse(day);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(
                new ErrorDto("Invalid day format. Expected one is 'yyyy-MM-DD' (example: 2022-06-20)"),
                HttpStatus.BAD_REQUEST
            );
        }

        var rates = exchangeRatesService.getRates(snapshotDate);
        return new ResponseEntity<>(rates, HttpStatus.OK);
    }

}
