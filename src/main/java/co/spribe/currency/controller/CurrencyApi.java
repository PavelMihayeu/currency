package co.spribe.currency.controller;

import co.spribe.currency.model.AddCurrencyRequest;
import co.spribe.currency.model.CurrencyData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

public interface CurrencyApi {

    @Operation(
            summary = "Retrieve a list of currencies",
            description = "Get a list of currencies used in the project.")
    @ApiResponse(responseCode = "200", content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    mediaType = MediaType.APPLICATION_JSON_VALUE)
    })
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    ResponseEntity<Set<String>> getCurrencyList();

    @Operation(
            summary = "Retrieve exchange rates for a currency",
            description = "Get exchange rates for a given currency code.")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = CurrencyData.class))})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    @GetMapping("/{code}")
    ResponseEntity<CurrencyData> getCurrencyExchangeRates(@PathVariable String code);

    @Operation(
            summary = "Add a new currency",
            description = "Add a new currency to the system.")

    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = CurrencyData.class))})
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
    @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    ResponseEntity<CurrencyData> addCurrency(@RequestBody AddCurrencyRequest internalCurrencyEntity);

}
