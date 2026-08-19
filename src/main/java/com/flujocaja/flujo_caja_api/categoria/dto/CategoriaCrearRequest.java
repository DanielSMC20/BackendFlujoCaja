package com.flujocaja.flujo_caja_api.categoria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoriaCrearRequest(

        @NotNull(
                message = "El tipo de movimiento es obligatorio."
        )
        Integer tipoMovimiento,

        @NotBlank(
                message = "El nombre de la categoría es obligatorio."
        )
        @Size(
                max = 100,
                message = "El nombre no puede superar los 100 caracteres."
        )
        String nombre,

        @Size(
                max = 250,
                message = "La descripción no puede superar los 250 caracteres."
        )
        String descripcion

) {
}