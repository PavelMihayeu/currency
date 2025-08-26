package co.spribe.currency.factory;

import co.spribe.currency.model.entity.Currency;
import co.spribe.currency.model.external.ExternalCurrency;

public interface CurrencyFactory<T extends Currency> {

    T createCurrency(ExternalCurrency externalCurrency);

    T createCurrency(String code);
}
