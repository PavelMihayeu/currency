package co.spribe.currency.service;

import co.spribe.currency.model.CurrencyData;

import java.util.Set;

public interface CurrencyService {

    Set<String> getCurrencies();

    CurrencyData getCurrencyExchangeRates(String code);

    CurrencyData addCurrency(String code);

    void updateRates();

}
