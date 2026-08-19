package com.flujocaja.flujo_caja_api.categoria.dto;

public record CategoriaResponse(

        Integer id,
        Integer tipoMovimiento,
        String nombre,
        String descripcion,
        Boolean activo

) {
}