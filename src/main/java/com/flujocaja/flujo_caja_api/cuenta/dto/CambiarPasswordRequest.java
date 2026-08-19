package com.flujocaja.flujo_caja_api.cuenta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarPasswordRequest(

        @NotBlank(
                message = "La contraseña actual es obligatoria."
        )
        String passwordActual,


        @NotBlank(
                message = "La nueva contraseña es obligatoria."
        )
        @Size(
                min = 8,
                max = 72,
                message = "La nueva contraseña debe tener entre 8 y 72 caracteres."
        )
        String nuevaPassword,


        @NotBlank(
                message = "Debe confirmar la nueva contraseña."
        )
        String confirmarPassword

) {
}