package com.flujocaja.flujo_caja_api.movimiento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MovimientoAnularRequest(

        @NotBlank(
                message = "El motivo de anulación es obligatorio."
        )
        @Size(
                max = 300,
                message = "El motivo de anulación no puede superar los 300 caracteres."
        )
        String motivo

) {
}