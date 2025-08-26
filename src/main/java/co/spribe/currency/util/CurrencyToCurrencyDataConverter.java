package co.spribe.currency.util;

import co.spribe.currency.model.CurrencyData;
import co.spribe.currency.model.entity.Currency;
import co.spribe.currency.model.entity.CurrencyRate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter<Currency, CurrencyData>;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CurrencyToCurrencyDataConverter implements Converter<Currency, CurrencyData> {

    @Override
    public CurrencyData convert(Currency source) {
        log.debug("Starting conversion of Currency to CurrencyData for Currency: {}", source.getCode());

        Map<String, BigDecimal> ratesMap = source.getCurrencyRates().stream()
                .collect(Collectors.toMap(CurrencyRate::getCode, CurrencyRate::getValue));

        log.debug("Constructed rates map: {}", ratesMap);

        CurrencyData currencyData = CurrencyData.builder()
                .baseCurrency(source.getCode())
                .lastUpdatedAt(ZonedDateTime.now())
                .rates(ratesMap)
                .build();

        log.info("Completed conversion of Currency to CurrencyData for Currency: {}", source.getCode());
        return currencyData;
    }
}
