package com.scartascini.currencyexchange.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorResponseTest {

    @Test
    public void testErrorResponse() {
        ErrorResponse response = new ErrorResponse("Bad Request", "Currency ETH not found");

        assertThat(response).isNotNull();
        assertThat(response.getError()).isEqualTo("Bad Request");
        assertThat(response.getMessage()).isEqualTo("Currency ETH not found");
    }
}
