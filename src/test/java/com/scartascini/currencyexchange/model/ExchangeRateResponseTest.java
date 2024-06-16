package com.scartascini.currencyexchange.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExchangeRateResponseTest {

    @Test
    public void testExchangeRateResponse() {
        ExchangeRateResponse response = new ExchangeRateResponse("BTC", "EUR", "61805.45");

        assertThat(response).isNotNull();
        assertThat(response.getCurrency1()).isEqualTo("BTC");
        assertThat(response.getCurrency2()).isEqualTo("EUR");
        assertThat(response.getExchangeRate()).isEqualTo("61805.45");
    }
}
