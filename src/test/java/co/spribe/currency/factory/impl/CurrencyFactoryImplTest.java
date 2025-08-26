package co.spribe.currency.factory.impl;

import co.spribe.currency.model.entity.Currency;
import co.spribe.currency.model.external.*;
import co.spribe.currency.service.ExternalCurrencyApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyFactoryImplTest {

    @Mock
    private ExternalCurrencyApi externalCurrencyApi;

    @InjectMocks
    private CurrencyFactoryImpl currencyFactory;

    private ExternalCurrency externalCurrency;
    private CurrencyRatesApiResponse ratesResponse;

    @BeforeEach
    public void setUp() {
        externalCurrency = new ExternalCurrency(
                "$", "United States Dollar", "$", 2, 0, "USD", "United States dollars", "fiat"
        );

        Meta meta = new Meta(ZonedDateTime.now());
        Rate rateData = new Rate("USD", BigDecimal.valueOf(1.0));
        ratesResponse = new CurrencyRatesApiResponse(meta, Map.of("USD", rateData));
    }

    @Test
    void testCreateCurrencyFromExternalCurrency_Success() {
        when(externalCurrencyApi.getLatestRates(anyString())).thenReturn(ratesResponse);

        Currency currency = currencyFactory.createCurrency(externalCurrency);

        assertNotNull(currency);
        assertEquals("USD", currency.getCode());
        assertEquals("$", currency.getSymbol());
        assertEquals("United States Dollar", currency.getName());
        assertEquals(1, currency.getCurrencyRates().size());
    }

    @Test
    void testCreateCurrency_Success() {
        when(externalCurrencyApi.getCurrencies(anyString())).thenReturn(new CurrencyApiResponse(Map.of("USD", externalCurrency)));
        when(externalCurrencyApi.getLatestRates(anyString())).thenReturn(ratesResponse);

        Currency currency = currencyFactory.createCurrency("USD");

        assertNotNull(currency);
        assertEquals("USD", currency.getCode());
        assertEquals("$", currency.getSymbol());
        assertEquals("United States Dollar", currency.getName());
        assertEquals(1, currency.getCurrencyRates().size());
    }

    @Test
    void testCreateCurrency_NotFound() {
        when(externalCurrencyApi.getCurrencies(anyString())).thenReturn(new CurrencyApiResponse(Map.of()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            currencyFactory.createCurrency("INVALID");
        });

        assertEquals("Currency with code INVALID not available", exception.getMessage());
    }
}
