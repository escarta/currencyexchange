package com.scartascini.currencyexchange.controller;

import com.scartascini.currencyexchange.exception.GlobalExceptionHandler;
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

public class ControllerTest {

    private MockMvc mockMvc;
    private AutoCloseable mocks;

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
        when(currencyExchangeService.getExchangeRate("BTC", "EUR")).thenReturn("61805.45");

        mockMvc.perform(get("/").param("currency1", "BTC").param("currency2", "EUR"))
                .andExpect(status().isOk())
                .andExpect(content().string("61805.45"));
    }

    @Test
    public void testGetCurrencyExchangeCurrencyNotFound() throws Exception {
        when(currencyExchangeService.getExchangeRate("ETH", "EUR")).thenThrow(new IllegalArgumentException("Currency ETH not found"));

        mockMvc.perform(get("/").param("currency1", "ETH").param("currency2", "EUR"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Currency ETH not found"));
    }

    @Test
    public void testGetCurrencyExchangeRateNotFound() throws Exception {
        when(currencyExchangeService.getExchangeRate("BTC", "GBP")).thenThrow(new IllegalArgumentException("Exchange rate for BTC to GBP not found"));

        mockMvc.perform(get("/").param("currency1", "BTC").param("currency2", "GBP"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Exchange rate for BTC to GBP not found"));
    }
}
