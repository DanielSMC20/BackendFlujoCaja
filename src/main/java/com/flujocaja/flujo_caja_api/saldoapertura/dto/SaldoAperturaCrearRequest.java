package com.flujocaja.flujo_caja_api.saldoapertura.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

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
        BigDecimal saldoInicial

) {
}
