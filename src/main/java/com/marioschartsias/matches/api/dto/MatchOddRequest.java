package com.marioschartsias.matches.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MatchOddRequest(
        @NotBlank @Size(max = 50) String specifier,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = 7, fraction = 3) BigDecimal odd
) {
}

