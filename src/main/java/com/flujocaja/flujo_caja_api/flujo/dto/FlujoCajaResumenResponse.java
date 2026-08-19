package com.flujocaja.flujo_caja_api.flujo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FlujoCajaResumenResponse(

        LocalDate fechaDesde,
        LocalDate fechaHasta,

        BigDecimal saldoInicialReal,
        BigDecimal saldoInicialProyectado,

        BigDecimal totalIngresos,
        BigDecimal totalEgresosPagados,
        BigDecimal totalEgresosProyectados,

        BigDecimal saldoFinalReal,
        BigDecimal saldoFinalProyectado

) {
}