package co.spribe.currency.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExternalCurrency(String symbol, String name, @JsonProperty("symbol_native") String symbolNative,
                               @JsonProperty("decimal_digits") int decimalDigits, int rounding, String code,
                               @JsonProperty("name_plural") String namePlural, String type) {
}
