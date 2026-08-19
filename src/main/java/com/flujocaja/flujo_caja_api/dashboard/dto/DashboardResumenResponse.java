package com.flujocaja.flujo_caja_api.dashboard.dto;

import java.math.BigDecimal;

public record DashboardResumenResponse(

        BigDecimal totalIngresos,

        BigDecimal totalEgresosPagados,

        BigDecimal totalEgresosProyectados,

        BigDecimal saldoReal,

        BigDecimal saldoProyectado

) {
}