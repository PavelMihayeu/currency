package co.spribe.currency.model;

import co.spribe.currency.util.ToUpperCaseDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record AddCurrencyRequest(@JsonDeserialize(using = ToUpperCaseDeserializer.class) String code) {
}
