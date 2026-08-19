package com.flujocaja.flujo_caja_api.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UltimoMovimientoResponse(

        Long id,

        LocalDate fecha,

        Integer tipoMovimiento,

        String tipoMovimientoDescripcion,

        Integer categoriaId,

        String categoria,

        String descripcion,

        BigDecimal monto,

        Boolean cancelado,

        String estado,

        Integer moneda,

        String monedaAbreviatura

) {
}