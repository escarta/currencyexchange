package com.scartascini.currencyexchange.controller;

import com.scartascini.currencyexchange.service.CurrencyExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class Controller {

    private final CurrencyExchangeService currencyExchangeService;

    @Autowired
    public Controller(CurrencyExchangeService currencyExchangeService) {
        this.currencyExchangeService = currencyExchangeService;
    }

    @GetMapping("/")
    public String getCurrencyExchange(
            @RequestParam(name = "currency1") String currency1,
            @RequestParam(name = "currency2") String currency2) throws IOException {

        validateCurrencyCode(currency1);
        validateCurrencyCode(currency2);

        return currencyExchangeService.getExchangeRate(currency1, currency2);
    }

    private void validateCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.isEmpty() || !currencyCode.matches("[A-Za-z]{3,10}")) {
            throw new IllegalArgumentException("Invalid currency code: " + currencyCode);
        }
        if (currencyCode.length() > 10) {
            throw new IllegalArgumentException("Currency code is too long: " + currencyCode);
        }
    }
}
