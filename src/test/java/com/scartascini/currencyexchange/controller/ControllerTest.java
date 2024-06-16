package com.scartascini.currencyexchange.controller;

import com.scartascini.currencyexchange.exception.GlobalExceptionHandler;
import com.scartascini.currencyexchange.model.ErrorResponse;
import com.scartascini.currencyexchange.model.ExchangeRateResponse;
import com.scartascini.currencyexchange.service.CurrencyExchangeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ControllerTest {

    private MockMvc mockMvc;
    private AutoCloseable mocks;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CurrencyExchangeService currencyExchangeService;

    @InjectMocks
    private Controller controller;

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void testGetCurrencyExchange() throws Exception {
        ExchangeRateResponse response = new ExchangeRateResponse("BTC", "EUR", "61805.45");
        when(currencyExchangeService.getExchangeRate("BTC", "EUR")).thenReturn("61805.45");

        String expectedResponse = objectMapper.writeValueAsString(response);

        mockMvc.perform(get("/exchange-rate/").param("currency1", "BTC").param("currency2", "EUR"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void testGetCurrencyExchangeCurrencyNotFound() throws Exception {
        when(currencyExchangeService.getExchangeRate("ETH", "EUR")).thenThrow(new IllegalArgumentException("Currency ETH not found"));

        ErrorResponse errorResponse = new ErrorResponse("Bad Request", "Currency ETH not found");
        String expectedResponse = objectMapper.writeValueAsString(errorResponse);

        mockMvc.perform(get("/exchange-rate/").param("currency1", "ETH").param("currency2", "EUR"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void testGetCurrencyExchangeRateNotFound() throws Exception {
        when(currencyExchangeService.getExchangeRate("BTC", "GBP")).thenThrow(new IllegalArgumentException("Exchange rate for BTC to GBP not found"));

        ErrorResponse errorResponse = new ErrorResponse("Bad Request", "Exchange rate for BTC to GBP not found");
        String expectedResponse = objectMapper.writeValueAsString(errorResponse);

        mockMvc.perform(get("/exchange-rate/").param("currency1", "BTC").param("currency2", "GBP"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedResponse));
    }
}
