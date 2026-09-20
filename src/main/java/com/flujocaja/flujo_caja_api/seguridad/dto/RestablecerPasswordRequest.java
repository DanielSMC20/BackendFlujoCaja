package com.flujocaja.flujo_caja_api.seguridad.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestablecerPasswordRequest(

        @NotBlank(
                message = "El token de recuperación es obligatorio."
        )
        String token,

        @NotBlank(
                message = "La nueva contraseña es obligatoria."
        )
        @Size(
                min = 8,
                max = 72,
                message = "La contraseña debe tener entre 8 y 72 caracteres."
        )
        String nuevaPassword,

        @NotBlank(
                message = "Debe confirmar la nueva contraseña."
        )
        String confirmarPassword

) {
}