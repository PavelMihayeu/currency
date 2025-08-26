package co.spribe.currency.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "currency_rate", schema = "currencyapi")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "currency_code", referencedColumnName = "code")
    private Currency currency;

    private String code;

    private BigDecimal value;
}
