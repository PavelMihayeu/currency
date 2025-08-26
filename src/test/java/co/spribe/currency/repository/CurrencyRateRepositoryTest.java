package co.spribe.currency.repository;

import co.spribe.currency.model.entity.Currency;
import co.spribe.currency.model.entity.CurrencyRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class CurrencyRateRepositoryTest {

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("password")
            .withInitScript("init_postgres.sql");

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @BeforeEach
    void setUp() {
        currencyRateRepository.deleteAll();
        currencyRepository.deleteAll();
    }

    @Test
    void saveAndFindById() {
        Currency currency = new Currency("USD", "$", "United States Dollar", "$", 2, 0, "dollars", "fiat", ZonedDateTime.now(), null);
        currencyRepository.save(currency);

        CurrencyRate currencyRate = new CurrencyRate(null, currency, "EUR", BigDecimal.valueOf(0.85));
        currencyRateRepository.save(currencyRate);

        assertTrue(currencyRateRepository.findById(currencyRate.getId()).isPresent());
        assertEquals("USD", currencyRate.getCurrency().getCode());
        assertEquals("EUR", currencyRate.getCode());
        assertEquals(BigDecimal.valueOf(0.85), currencyRate.getValue());
    }
}
