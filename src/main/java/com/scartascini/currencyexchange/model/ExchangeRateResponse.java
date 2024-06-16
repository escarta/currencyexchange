package com.scartascini.currencyexchange.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeRateResponse {
    private String currency1;
    private String currency2;
    private String exchangeRate;
}
