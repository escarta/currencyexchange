package com.scartascini.currencyexchange.service.impl;

import com.scartascini.currencyexchange.service.CurrencyExchangeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Service
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyExchangeServiceImpl.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ticker.url}")
    private String tickerUrl;

    public CurrencyExchangeServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getExchangeRate(String currency1, String currency2) throws IOException {
        validateCurrencyCode(currency1);
        validateCurrencyCode(currency2);

        logger.info("Fetching exchange rate for {} to {}", currency1, currency2);
        String jsonResponse = restTemplate.getForObject(tickerUrl, String.class);

        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode currencyNode = rootNode.path(currency1);

        if (currencyNode.isMissingNode() || currencyNode.isNull()) {
            logger.error("Currency {} not found", currency1);
            throw new IllegalArgumentException("Currency " + currency1 + " not found");
        }

        JsonNode exchangeRateNode = currencyNode.path(currency2);

        if (exchangeRateNode.isMissingNode() || exchangeRateNode.isNull()) {
            logger.error("Exchange rate for {} to {} not found", currency1, currency2);
            throw new IllegalArgumentException("Exchange rate for " + currency1 + " to " + currency2 + " not found");
        }

        String rate = exchangeRateNode.asText();
        logger.info("Exchange rate for {} to {} is {}", currency1, currency2, rate);
        return rate;
    }

    private void validateCurrencyCode(String currencyCode) {
        if (currencyCode == null || !currencyCode.matches("^[A-Z0-9]{1,10}$")) {
            throw new IllegalArgumentException("Currency code " + currencyCode + " is not valid");
        }
    }
}
