package com.scartascini.currencyexchange.service;

import com.scartascini.currencyexchange.service.impl.CurrencyExchangeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CurrencyExchangeServiceTest implements AutoCloseable {

    @InjectMocks
    private CurrencyExchangeServiceImpl currencyExchangeService;

    @Mock
    private RestTemplate restTemplate;

    @Value("${ticker.url}")
    private String tickerUrl;

    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        // Set the tickerUrl value for the service
        ReflectionTestUtils.setField(currencyExchangeService, "tickerUrl", tickerUrl);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void testGetExchangeRate() throws IOException {
        String jsonResponse = "{\"BTC\": {\"EUR\": \"61805.45\", \"USD\": \"66247.19\"}}";

        when(restTemplate.getForObject(tickerUrl, String.class)).thenReturn(jsonResponse);

        String rate = currencyExchangeService.getExchangeRate("BTC", "EUR");
        assertNotNull(rate);  // Ensure that rate is not null
    }

    @Test
    public void testGetExchangeRateCurrencyNotFound() throws IOException {
        String jsonResponse = "{\"BTC\": {\"EUR\": \"61805.45\", \"USD\": \"66247.19\"}}";

        when(restTemplate.getForObject(tickerUrl, String.class)).thenReturn(jsonResponse);

        try {
            currencyExchangeService.getExchangeRate("ETH", "EUR");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testGetExchangeRateRateNotFound() throws IOException {
        String jsonResponse = "{\"BTC\": {\"EUR\": \"61805.45\", \"USD\": \"66247.19\"}}";

        when(restTemplate.getForObject(tickerUrl, String.class)).thenReturn(jsonResponse);

        try {
            currencyExchangeService.getExchangeRate("BTC", "GBP");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testInvalidCurrencyCode() throws IOException {
        try {
            currencyExchangeService.getExchangeRate("BTC$#@", "EUR");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        mocks.close();
    }
}
