package com.flujocaja.flujo_caja_api.usuario.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioEstadoRequest(

        @NotNull(
                message = "El estado es obligatorio."
        )
        Boolean activo

) {
}