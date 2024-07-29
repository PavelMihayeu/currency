package co.spribe.currency.service;

import co.spribe.currency.config.FeignConfig;
import co.spribe.currency.model.external.CurrencyApiResponse;
import co.spribe.currency.model.external.CurrencyRatesApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${external.currency.api.name}", url = "${external.currency.api.url}", configuration = FeignConfig.class)
public interface ExternalCurrencyApi {

    @RequestMapping(method = RequestMethod.GET, value = "/currencies")
    CurrencyApiResponse getCurrencies(@RequestParam("currencies") String currencies);

    @RequestMapping(method = RequestMethod.GET, value = "/latest")
    CurrencyRatesApiResponse getLatestRates(@RequestParam("base_currency") String baseCurrency);
}
