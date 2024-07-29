package co.spribe.currency.service.impl;

import co.spribe.currency.factory.impl.CurrencyFactoryImpl;
import co.spribe.currency.model.CurrencyData;
import co.spribe.currency.model.entity.Currency;
import co.spribe.currency.repository.CurrencyRateRepository;
import co.spribe.currency.repository.CurrencyRepository;
import co.spribe.currency.service.CurrencyService;
import co.spribe.currency.service.ExternalCurrencyApi;
import co.spribe.currency.util.CurrencyToCurrencyDataConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Value("${exchange.rate.cache.ttl.seconds}")
    private int cacheTtlSeconds;

    private final ExternalCurrencyApi externalCurrencyApi;
    private final CurrencyRepository currencyRepository;
    private final CurrencyRateRepository currencyRateRepository;
    private final CurrencyToCurrencyDataConverter converter;
    private final CurrencyFactoryImpl currencyFactory;
    private final Map<String, CurrencyData> currencyDataMap = new ConcurrentHashMap<>();

    public CurrencyServiceImpl(ExternalCurrencyApi externalCurrencyApi,
                               CurrencyRateRepository currencyRateRepository,
                               CurrencyRepository currencyRepository,
                               CurrencyToCurrencyDataConverter converter,
                               CurrencyFactoryImpl currencyFactory) {
        this.externalCurrencyApi = externalCurrencyApi;
        this.currencyRateRepository = currencyRateRepository;
        this.currencyRepository = currencyRepository;
        this.converter = converter;
        this.currencyFactory = currencyFactory;
    }

    @Override
    public Set<String> getCurrencies() {
        log.debug("Fetching all currency codes from the currencyDataMap");
        return currencyDataMap.keySet();
    }

    @Override
    public CurrencyData getCurrencyExchangeRates(String code) {
        log.debug("Fetching exchange rates for currency code: {}", code);

        CurrencyData currencyData = currencyDataMap.get(code.toUpperCase());

        if (currencyData != null && currencyData.lastUpdatedAt().isAfter(ZonedDateTime.now().minusSeconds(cacheTtlSeconds))) {
            return currencyData;
        } else {
            Currency currencyEntity = currencyRepository.findByCode(code)
                    .orElseThrow(() -> new IllegalArgumentException("Currency with code " + code + " not found"));

            return currencyDataMap.put(code.toUpperCase(), converter.convert(currencyEntity));
        }
    }

    @Transactional
    @Override
    public CurrencyData addCurrency(String code) {
        log.info("Adding currency with code: {}", code);
        if (currencyRepository.existsByCode(code)) {
            log.warn("Currency with code {} already exists", code);
            throw new IllegalArgumentException("Currency with code " + code + " already exists");
        }

        log.debug("Creating currency entity for code: {}", code);
        Currency currencyEntity = currencyRepository.save(currencyFactory.createCurrency(code));

        log.debug("Converting currency entity to currency data for code: {}", code);
        CurrencyData currencyData = converter.convert(currencyEntity);
        currencyDataMap.putIfAbsent(currencyEntity.getCode(), currencyData);

        log.info("Currency with code {} added successfully", code);
        return currencyData;
    }

    @Transactional
    @Scheduled(initialDelay = 0, fixedRate = 3600000)
    public void updateRates() {
        log.info("Updating currency rates");
        Set<String> currencyCodes = currencyRepository.findAllCodes();

        if (currencyCodes.isEmpty()) {
            log.warn("No currency codes found to update.");
            return;
        }

        String codes = String.join(",", currencyCodes);
        log.debug("Fetching latest rates for codes: {}", codes);

        List<Currency> currencies = externalCurrencyApi.getCurrencies(codes).data().values().stream()
                .map(currencyFactory::createCurrency)
                .toList();

        log.debug("Deleting all existing currency rates and data");
        currencyRateRepository.deleteAll();
        currencyRepository.deleteAll();
        currencyDataMap.clear();

        log.debug("Saving updated currency data");
        currencyRepository.saveAll(currencies);
        currencies.forEach(currency -> currencyDataMap.putIfAbsent(currency.getCode(), converter.convert(currency)));

        log.info("Currency rates updated successfully");
    }
}
