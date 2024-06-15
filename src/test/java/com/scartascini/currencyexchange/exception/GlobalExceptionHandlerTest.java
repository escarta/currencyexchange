package com.scartascini.currencyexchange.exception;

import com.scartascini.currencyexchange.controller.Controller;
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

public class GlobalExceptionHandlerTest implements AutoCloseable {

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
    public void testHandleIllegalArgumentException() throws Exception {
        when(currencyExchangeService.getExchangeRate("ETH", "EUR")).thenThrow(new IllegalArgumentException("Currency ETH not found"));

        mockMvc.perform(get("/").param("currency1", "ETH").param("currency2", "EUR"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Currency ETH not found"));
    }

    @Test
    public void testHandleGlobalException() throws Exception {
        when(currencyExchangeService.getExchangeRate("BTC", "EUR")).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/").param("currency1", "BTC").param("currency2", "EUR"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while processing your request"));
    }

    @Override
    public void close() throws Exception {
        mocks.close();
    }
}
