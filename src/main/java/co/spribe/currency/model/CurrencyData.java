package co.spribe.currency.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"base_currency", "rates", "last_updated_at"})
public record CurrencyData(
        @JsonProperty("base_currency") String baseCurrency,
        @JsonProperty("rates") Map<String, BigDecimal> rates,
        @JsonProperty("last_updated_at")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") ZonedDateTime lastUpdatedAt) {
}
