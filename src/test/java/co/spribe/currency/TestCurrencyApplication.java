package co.spribe.currency;

import org.springframework.boot.SpringApplication;

public class TestCurrencyApplication {

    public static void main(String[] args) {
        SpringApplication.from(CurrencyApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
