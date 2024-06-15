package com.scartascini.currencyexchange.service;

import java.io.IOException;

public interface CurrencyExchangeService {
    String getExchangeRate(String currency1, String currency2) throws IOException;
}
