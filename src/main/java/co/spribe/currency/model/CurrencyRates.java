package co.spribe.currency.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;

public record CurrencyRates(ZonedDateTime lastUpdatedAt, Map<String, BigDecimal> rates) {
}
