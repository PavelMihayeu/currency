package co.spribe.currency.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "currency", schema = "currencyapi")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "code")
public class Currency {

    @Id
    private String code;

    private String symbol;

    private String name;

    @Column(name = "symbol_native")
    private String symbolNative;

    @Column(name = "decimal_digits")
    private int decimalDigits;

    private int rounding;

    @Column(name = "name_plural")
    private String namePlural;

    private String type;

    private ZonedDateTime lastUpdatedAt;

    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<CurrencyRate> currencyRates;

}