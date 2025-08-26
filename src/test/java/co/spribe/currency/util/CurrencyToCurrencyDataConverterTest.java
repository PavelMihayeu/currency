package co.spribe.currency.util;

import co.spribe.currency.model.CurrencyData;
import co.spribe.currency.model.entity.Currency;
import co.spribe.currency.model.entity.CurrencyRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyToCurrencyDataConverterTest {

    private CurrencyToCurrencyDataConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new CurrencyToCurrencyDataConverter();
    }

    @Test
    void testConvert() {
        ZonedDateTime lastUpdatedAt = ZonedDateTime.now();
        Currency currency = createCurrency(lastUpdatedAt);

        CurrencyRate rate = new CurrencyRate();
        rate.setCurrency(currency);
        rate.setCode("EUR");
        rate.setValue(BigDecimal.valueOf(0.85));

        currency.setCurrencyRates(Collections.singletonList(rate));

        CurrencyData currencyData = converter.convert(currency);

        assertNotNull(currencyData);
        assertEquals("USD", currencyData.baseCurrency());
        assertTrue(currencyData.lastUpdatedAt().isAfter(lastUpdatedAt.minusSeconds(1)) && currencyData.lastUpdatedAt().isBefore(lastUpdatedAt.plusSeconds(1)));
        assertNotNull(currencyData.rates());
        assertEquals(1, currencyData.rates().size());
        assertEquals(BigDecimal.valueOf(0.85), currencyData.rates().get("EUR"));
    }

    @Test
    void testConvert_WithEmptyRates() {
        ZonedDateTime lastUpdatedAt = ZonedDateTime.now();
        Currency currency = createCurrency(lastUpdatedAt);
        currency.setCurrencyRates(Collections.emptyList());

        CurrencyData currencyData = converter.convert(currency);

        assertNotNull(currencyData);
        assertEquals("USD", currencyData.baseCurrency());
        assertTrue(currencyData.lastUpdatedAt().isAfter(lastUpdatedAt.minusSeconds(1)) && currencyData.lastUpdatedAt().isBefore(lastUpdatedAt.plusSeconds(1)));
        assertNotNull(currencyData.rates());
        assertEquals(0, currencyData.rates().size());
    }

    private Currency createCurrency(ZonedDateTime lastUpdatedAt) {
        Currency currency = new Currency();
        currency.setCode("USD");
        currency.setSymbol("$");
        currency.setName("United States Dollar");
        currency.setSymbolNative("$");
        currency.setDecimalDigits(2);
        currency.setRounding(0);
        currency.setNamePlural("United States dollars");
        currency.setType("fiat");
        currency.setLastUpdatedAt(lastUpdatedAt);
        return currency;
    }
}
