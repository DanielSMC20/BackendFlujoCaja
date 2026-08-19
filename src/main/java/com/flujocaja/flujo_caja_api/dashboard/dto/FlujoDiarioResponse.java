package com.flujocaja.flujo_caja_api.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FlujoDiarioResponse(

        LocalDate fecha,

        BigDecimal ingresos,

        BigDecimal egresosPagados,

        BigDecimal egresosProyectados,

        BigDecimal flujoReal,

        BigDecimal flujoProyectado,

        BigDecimal saldoAcumuladoReal,

        BigDecimal saldoAcumuladoProyectado

) {
}