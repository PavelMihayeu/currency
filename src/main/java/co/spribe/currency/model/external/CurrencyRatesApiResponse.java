package co.spribe.currency.model.external;

import java.util.Map;

public record CurrencyRatesApiResponse(Meta meta, Map<String, Rate> data) {
}
