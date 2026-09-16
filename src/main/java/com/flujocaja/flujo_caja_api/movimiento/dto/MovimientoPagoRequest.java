package com.flujocaja.flujo_caja_api.movimiento.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MovimientoPagoRequest(

        @NotNull(
                message = "La fecha de pago es obligatoria."
        )
        LocalDate fechaPago

) {
}