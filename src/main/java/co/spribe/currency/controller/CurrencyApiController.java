package co.spribe.currency.controller;

import co.spribe.currency.model.AddCurrencyRequest;
import co.spribe.currency.model.CurrencyData;
import co.spribe.currency.service.CurrencyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.function.Supplier;

@Slf4j
@RestController
@RequestMapping("/currency")
public class CurrencyApiController implements CurrencyApi {

    private final CurrencyService currencyService;
    private final HttpServletRequest request;

    public CurrencyApiController(CurrencyService currencyService, HttpServletRequest request) {
        this.currencyService = currencyService;
        this.request = request;
    }

    @Override
    @GetMapping
    public ResponseEntity<Set<String>> getCurrencyList() {
        return handleResponse(currencyService::getCurrencies);
    }

    @Override
    @GetMapping("/{code}")
    public ResponseEntity<CurrencyData> getCurrencyExchangeRates(@PathVariable String code) {
        return handleResponse(() -> currencyService.getCurrencyExchangeRates(code));
    }

    @Override
    @PostMapping
    public ResponseEntity<CurrencyData> addCurrency(AddCurrencyRequest addCurrencyRequest) {
        return handleResponse(() -> currencyService.addCurrency(addCurrencyRequest.code()));
    }

    private <T> ResponseEntity<T> handleResponse(Supplier<T> supplier) {
        log.info("Handling {} response for {}", request.getMethod(), request.getRequestURI());
        try {
            T result = supplier.get();
            log.info("Response handled successfully");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.error("Bad request error while handling response", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error while handling response", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}