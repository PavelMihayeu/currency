package co.spribe.currency.factory.impl;

import co.spribe.currency.factory.CurrencyFactory;
import co.spribe.currency.model.entity.Currency;
import co.spribe.currency.model.entity.CurrencyRate;
import co.spribe.currency.model.external.CurrencyRatesApiResponse;
import co.spribe.currency.model.external.ExternalCurrency;
import co.spribe.currency.service.ExternalCurrencyApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CurrencyFactoryImpl implements CurrencyFactory<Currency> {

    private final ExternalCurrencyApi externalCurrencyApi;

    public CurrencyFactoryImpl(ExternalCurrencyApi externalCurrencyApi) {
        this.externalCurrencyApi = externalCurrencyApi;
    }

    @Override
    public Currency createCurrency(ExternalCurrency externalCurrency) {
        log.debug("Creating Currency entity from ExternalCurrency: {}", externalCurrency);

        CurrencyRatesApiResponse rates = externalCurrencyApi.getLatestRates(externalCurrency.code());
        log.debug("Retrieved latest rates for currency {}: {}", externalCurrency.code(), rates);

        Currency currency = new Currency();
        BeanUtils.copyProperties(externalCurrency, currency);

        List<CurrencyRate> currencyRates = getRateList(rates, currency);

        currency.setLastUpdatedAt(rates.meta().lastUpdatedAt());
        currency.setCurrencyRates(currencyRates);

        log.info("Created Currency entity: {}", currency);
        return currency;
    }

    @Override
    public Currency createCurrency(String code) {
        log.debug("Creating Currency entity for code: {}", code);

        ExternalCurrency externalCurrency = externalCurrencyApi.getCurrencies(code).data().values().stream()
                .filter(currency -> currency.code().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Currency with code " + code + " not available"));

        log.debug("Retrieved ExternalCurrency for code {}: {}", code, externalCurrency);

        return this.createCurrency(externalCurrency);
    }

    private List<CurrencyRate> getRateList(CurrencyRatesApiResponse rates, Currency currencyEntity) {
        log.debug("Generating rate list for Currency entity: {}", currencyEntity);

        List<CurrencyRate> currencyRates = rates.data().values()
                .stream()
                .map(externalRate -> {
                    CurrencyRate rate = new CurrencyRate();
                    rate.setCurrency(currencyEntity);
                    rate.setCode(externalRate.code());
                    rate.setValue(externalRate.value());
                    return rate;
                }).toList();

        log.debug("Generated CurrencyRate list: {}", currencyRates);
        return currencyRates;
    }
}
