package com.flujocaja.flujo_caja_api.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioActualizarRequest(

        @NotBlank(
                message = "Los nombres son obligatorios."
        )
        @Size(max = 100)
        String nombres,


        @NotBlank(
                message = "Los apellidos son obligatorios."
        )
        @Size(max = 150)
        String apellidos,


        @NotNull(
                message = "El rol es obligatorio."
        )
        Integer rolId

) {
}