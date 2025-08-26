package co.spribe.currency.controller;

import co.spribe.currency.model.CurrencyData;
import co.spribe.currency.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CurrencyApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CurrencyApiController currencyApiController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(currencyApiController).build();
    }

    @Test
    void testGetCurrencyList() throws Exception {
        Set<String> currencies = new HashSet<>();
        currencies.add("USD");
        currencies.add("EUR");

        when(currencyService.getCurrencies()).thenReturn(currencies);

        mockMvc.perform(get("/currency"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", containsInAnyOrder("USD", "EUR")));
    }

    @Test
    void testGetCurrencyExchangeRates() throws Exception {
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("EUR", BigDecimal.valueOf(0.85));
        rates.put("GBP", BigDecimal.valueOf(0.75));
        CurrencyData currencyData = CurrencyData.builder()
                .baseCurrency("USD")
                .rates(rates)
                .lastUpdatedAt(ZonedDateTime.parse("2023-07-01T00:00:00Z"))
                .build();

        when(currencyService.getCurrencyExchangeRates(anyString())).thenReturn(currencyData);

        mockMvc.perform(get("/currency/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base_currency").value("USD"))
                .andExpect(jsonPath("$.rates.EUR").value(0.85))
                .andExpect(jsonPath("$.rates.GBP").value(0.75))
                .andExpect(jsonPath("$.last_updated_at").value("2023-07-01T00:00:00Z"));
    }

    @Test
    void testAddCurrency() throws Exception {
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("USD", BigDecimal.valueOf(1.0));
        CurrencyData currencyData = CurrencyData.builder()
                .baseCurrency("GBP")
                .rates(rates)
                .lastUpdatedAt(ZonedDateTime.parse("2023-07-01T00:00:00Z"))
                .build();

        when(currencyService.addCurrency(anyString())).thenReturn(currencyData);

        mockMvc.perform(post("/currency")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"GBP\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base_currency").value("GBP"))
                .andExpect(jsonPath("$.rates.USD").value(1.0))
                .andExpect(jsonPath("$.last_updated_at").value("2023-07-01T00:00:00Z"));
    }

    @Test
    void testGetCurrencyExchangeRates_NotFound() throws Exception {
        when(currencyService.getCurrencyExchangeRates(anyString())).thenThrow(new IllegalArgumentException("Currency not found"));

        mockMvc.perform(get("/currency/ABC"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddCurrency_BadRequest() throws Exception {
        when(currencyService.addCurrency(anyString())).thenThrow(new IllegalArgumentException("Invalid currency code"));

        mockMvc.perform(post("/currency")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"INVALID\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddCurrency_InternalServerError() throws Exception {
        when(currencyService.addCurrency(anyString())).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/currency")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"GBP\"}"))
                .andExpect(status().isInternalServerError());
    }
}
