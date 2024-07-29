package co.spribe.currency.model.external;

import java.util.Map;

public record CurrencyApiResponse(Map<String, ExternalCurrency> data) {
}
