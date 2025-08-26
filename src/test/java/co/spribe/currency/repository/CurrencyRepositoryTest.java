package co.spribe.currency.repository;

import co.spribe.currency.model.entity.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("password")
            .withInitScript("init_postgres.sql");

    @BeforeEach
    void setUp() {
        currencyRepository.deleteAll();
    }

    @Test
    void saveAndFindByCode() {
        Currency currency = new Currency("USD", "$", "United States Dollar", "$", 2, 0, "dollars", "fiat", ZonedDateTime.now(), null);
        currencyRepository.save(currency);

        Optional<Currency> foundCurrency = currencyRepository.findByCode("USD");

        assertTrue(foundCurrency.isPresent());
        assertEquals(currency, foundCurrency.get());
    }

    @Test
    void findAllCodes() {
        Currency currency1 = new Currency("USD", "$", "United States Dollar", "$", 2, 0, "dollars", "fiat", ZonedDateTime.now(), null);
        Currency currency2 = new Currency("EUR", "€", "Euro", "€", 2, 0, "euros", "fiat", ZonedDateTime.now(), null);
        currencyRepository.save(currency1);
        currencyRepository.save(currency2);

        Set<String> codes = currencyRepository.findAllCodes();

        assertTrue(codes.contains("USD"));
        assertTrue(codes.contains("EUR"));
    }

    @Test
    void existsByCode() {
        Currency currency = new Currency("USD", "$", "United States Dollar", "$", 2, 0, "dollars", "fiat", ZonedDateTime.now(), null);
        currencyRepository.save(currency);

        assertTrue(currencyRepository.existsByCode("USD"));
        assertFalse(currencyRepository.existsByCode("EUR"));
    }
}
