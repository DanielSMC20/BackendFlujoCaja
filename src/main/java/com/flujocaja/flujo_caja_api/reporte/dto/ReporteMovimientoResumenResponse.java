package com.flujocaja.flujo_caja_api.reporte.dto;

import java.math.BigDecimal;

public record ReporteMovimientoResumenResponse(

        Integer cantidadRegistros,

        Integer cantidadAnulados,

        BigDecimal totalIngresos,

        BigDecimal totalEgresosPagados,

        BigDecimal totalEgresosProyectados

) {
}