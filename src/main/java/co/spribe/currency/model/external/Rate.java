package co.spribe.currency.model.external;

import java.math.BigDecimal;

public record Rate(String code, BigDecimal value) {
}