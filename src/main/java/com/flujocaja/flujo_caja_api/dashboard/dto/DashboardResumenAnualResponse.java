package com.flujocaja.flujo_caja_api.dashboard.dto;

import java.math.BigDecimal;

public record DashboardResumenAnualResponse(

        Integer anio,

        BigDecimal saldoInicial,

        BigDecimal ingresosCorrientes,

        BigDecimal ingresosNoCorrientes,

        BigDecimal ingresosFinancieros,

        BigDecimal totalIngresos,

        BigDecimal egresosCorrientes,

        BigDecimal egresosNoCorrientes,

        BigDecimal egresosFinancieros,

        BigDecimal totalEgresos,

        BigDecimal saldoFinal,

        BigDecimal saldoOperativo

) {
}