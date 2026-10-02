package com.flujocaja.flujo_caja_api.saldoapertura.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaldoAperturaCrearRequest(

        @NotNull(
                message = "El saldo inicial es obligatorio."
        )
        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "El saldo inicial no puede ser negativo."
        )
        @Digits(
                integer = 16,
                fraction = 2,
                message = "El saldo inicial debe tener como máximo 16 enteros y 2 decimales."
        )
        BigDecimal saldoInicial,

        @NotNull(
                message = "La fecha de apertura es obligatoria."
        )
        @PastOrPresent(
                message = "La fecha de apertura no puede ser futura."
        )
        LocalDate fechaApertura

) {
}