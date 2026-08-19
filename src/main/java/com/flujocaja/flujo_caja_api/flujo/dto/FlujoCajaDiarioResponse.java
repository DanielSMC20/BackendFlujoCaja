package com.flujocaja.flujo_caja_api.flujo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FlujoCajaDiarioResponse(

        LocalDate fecha,

        BigDecimal ingresos,

        BigDecimal egresosPagados,

        BigDecimal egresosProyectados,

        BigDecimal flujoReal,

        BigDecimal flujoProyectado,

        BigDecimal saldoReal,

        BigDecimal saldoProyectado

) {
}