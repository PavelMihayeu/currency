package co.spribe.currency.service.impl;

import co.spribe.currency.factory.impl.CurrencyFactoryImpl;
import co.spribe.currency.model.CurrencyData;
import co.spribe.currency.model.entity.Currency;
import co.spribe.currency.model.external.CurrencyApiResponse;
import co.spribe.currency.model.external.ExternalCurrency;
import co.spribe.currency.repository.CurrencyRateRepository;
import co.spribe.currency.repository.CurrencyRepository;
import co.spribe.currency.service.ExternalCurrencyApi;
import co.spribe.currency.util.CurrencyToCurrencyDataConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private ExternalCurrencyApi externalCurrencyApi;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private CurrencyRateRepository currencyRateRepository;

    @Mock
    private CurrencyToCurrencyDataConverter converter;

    @Mock
    private CurrencyFactoryImpl currencyFactory;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    private Currency currency;
    private CurrencyData currencyData;
    private final int cacheTtlSeconds = 3600;

    @BeforeEach
    void setUp() {
        currency = new Currency("USD", "$", "United States Dollar", "$", 2, 0, "dollars", "fiat", ZonedDateTime.now(), null);
        currencyData = CurrencyData.builder()
                .baseCurrency("USD")
                .rates(Map.of("EUR", BigDecimal.valueOf(0.85)))
                .lastUpdatedAt(ZonedDateTime.now())
                .build();
        ReflectionTestUtils.setField(currencyService, "cacheTtlSeconds", cacheTtlSeconds);
    }

    @Test
    void getCurrencies() {
        Map<String, CurrencyData> currencyDataMap = new HashMap<>();
        currencyDataMap.put("USD", currencyData);
        ReflectionTestUtils.setField(currencyService, "currencyDataMap", currencyDataMap);

        Set<String> currencies = currencyService.getCurrencies();
        assertTrue(currencies.contains("USD"));
    }

    @Test
    void getCurrencyExchangeRates() {
        Map<String, CurrencyData> currencyDataMap = new HashMap<>();
        currencyDataMap.put("USD", currencyData);
        ReflectionTestUtils.setField(currencyService, "currencyDataMap", currencyDataMap);

        CurrencyData result = currencyService.getCurrencyExchangeRates("USD");
        assertNotNull(result);
        assertEquals("USD", result.baseCurrency());
    }

    @Test
    void getCurrencyExchangeRatesNotFound() {
        Map<String, CurrencyData> currencyDataMap = new HashMap<>();
        ReflectionTestUtils.setField(currencyService, "currencyDataMap", currencyDataMap);

        assertThrows(IllegalArgumentException.class, () -> currencyService.getCurrencyExchangeRates("EUR"));
    }

    @Test
    void getCurrencyExchangeRatesCacheExpired() {
        CurrencyData staleCurrencyData = CurrencyData.builder()
                .baseCurrency("USD")
                .rates(Map.of("EUR", BigDecimal.valueOf(0.85)))
                .lastUpdatedAt(ZonedDateTime.now().minusSeconds(cacheTtlSeconds + 1))
                .build();
        Map<String, CurrencyData> currencyDataMap = new HashMap<>();
        currencyDataMap.put("USD", staleCurrencyData);
        ReflectionTestUtils.setField(currencyService, "currencyDataMap", currencyDataMap);

        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(currency));
        when(converter.convert(currency)).thenReturn(currencyData);

        CurrencyData result = currencyService.getCurrencyExchangeRates("USD");

        assertNotNull(result);
        assertEquals("USD", result.baseCurrency());
        verify(currencyRepository, times(1)).findByCode("USD");
    }

    @Test
    void addCurrency() {
        when(currencyRepository.existsByCode("USD")).thenReturn(false);
        when(currencyFactory.createCurrency("USD")).thenReturn(currency);
        when(currencyRepository.save(currency)).thenReturn(currency);
        when(converter.convert(currency)).thenReturn(currencyData);

        CurrencyData result = currencyService.addCurrency("USD");

        assertNotNull(result);
        assertEquals("USD", result.baseCurrency());
        verify(currencyRepository, times(1)).save(currency);
    }

    @Test
    void addCurrencyAlreadyExists() {
        when(currencyRepository.existsByCode("USD")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> currencyService.addCurrency("USD"));
    }

    @Test
    void updateRates() {
        ExternalCurrency externalCurrency = new ExternalCurrency("$", "United States Dollar", "$", 2, 0, "USD", "dollars", "fiat");
        CurrencyApiResponse currencyApiResponse = new CurrencyApiResponse(Map.of("USD", externalCurrency));

        when(currencyRepository.findAllCodes()).thenReturn(Set.of("USD", "EUR"));
        when(externalCurrencyApi.getCurrencies(anyString())).thenReturn(currencyApiResponse);
        when(currencyFactory.createCurrency(externalCurrency)).thenReturn(currency);
        when(converter.convert(any())).thenReturn(currencyData);

        currencyService.updateRates();

        verify(currencyRateRepository, times(1)).deleteAll();
        verify(currencyRepository, times(1)).deleteAll();
        verify(currencyRepository, times(1)).saveAll(anyList());
        assertTrue(ReflectionTestUtils.getField(currencyService, "currencyDataMap") instanceof Map);
    }

    @Test
    void updateRatesNoCurrencies() {
        when(currencyRepository.findAllCodes()).thenReturn(Collections.emptySet());

        currencyService.updateRates();

        verify(currencyRateRepository, never()).deleteAll();
        verify(currencyRepository, never()).deleteAll();
    }
}
