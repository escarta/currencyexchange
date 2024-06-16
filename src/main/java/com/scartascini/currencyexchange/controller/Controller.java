package com.scartascini.currencyexchange.controller;

import com.scartascini.currencyexchange.model.ExchangeRateResponse;
import com.scartascini.currencyexchange.model.ErrorResponse;
import com.scartascini.currencyexchange.service.CurrencyExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("/exchange-rate")
@RestController
public class Controller {

    private final CurrencyExchangeService currencyExchangeService;

    @Autowired
    public Controller(CurrencyExchangeService currencyExchangeService) {
        this.currencyExchangeService = currencyExchangeService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getCurrencyExchange(
            @RequestParam(name = "currency1") String currency1,
            @RequestParam(name = "currency2") String currency2) {

        try {
            String exchangeRate = currencyExchangeService.getExchangeRate(currency1, currency2);
            return ResponseEntity.ok(new ExchangeRateResponse(currency1, currency2, exchangeRate));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Bad Request", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Internal Server Error", "An error occurred while processing your request"));
        }
    }
}
