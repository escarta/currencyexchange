package com.scartascini.currencyexchange.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scartascini.currencyexchange.controller.Controller;
import com.scartascini.currencyexchange.model.ErrorResponse;
import com.scartascini.currencyexchange.service.CurrencyExchangeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GlobalExceptionHandlerTest {

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
    public void testHandleIllegalArgumentException() throws Exception {
        when(currencyExchangeService.getExchangeRate("ETH", "EUR")).thenThrow(new IllegalArgumentException("Currency ETH not found"));

        ErrorResponse expectedResponse = new ErrorResponse("Bad Request", "Currency ETH not found");
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);

        mockMvc.perform(get("/exchange-rate/").param("currency1", "ETH").param("currency2", "EUR"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedResponseBody));
    }

    @Test
    public void testHandleIOException() throws Exception {
        when(currencyExchangeService.getExchangeRate("BTC", "EUR")).thenThrow(new IOException("IO error"));

        ErrorResponse expectedResponse = new ErrorResponse("Internal Server Error", "An error occurred while processing your request");
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);

        mockMvc.perform(get("/exchange-rate/").param("currency1", "BTC").param("currency2", "EUR"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(expectedResponseBody));
    }

    @Test
    public void testHandleRuntimeException() throws Exception {
        when(currencyExchangeService.getExchangeRate("BTC", "EUR")).thenThrow(new RuntimeException("Unexpected error"));

        ErrorResponse expectedResponse = new ErrorResponse("Internal Server Error", "Unexpected error");
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);

        mockMvc.perform(get("/exchange-rate/").param("currency1", "BTC").param("currency2", "EUR"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(expectedResponseBody));
    }

    @Test
    public void testHandleMissingServletRequestParameterExceptionCurrency1() throws Exception {
        ErrorResponse expectedResponse = new ErrorResponse("Bad Request", "Required request parameter 'currency1' is not present");
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);

        mockMvc.perform(get("/exchange-rate/").param("currency2", "EUR"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedResponseBody));
    }

    @Test
    public void testHandleMissingServletRequestParameterExceptionCurrency2() throws Exception {
        ErrorResponse expectedResponse = new ErrorResponse("Bad Request", "Required request parameter 'currency2' is not present");
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);

        mockMvc.perform(get("/exchange-rate/").param("currency1", "BTC"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedResponseBody));
    }
}
