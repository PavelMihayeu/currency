package co.spribe.currency.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public record Meta(@JsonProperty("last_updated_at") ZonedDateTime lastUpdatedAt) {
}
