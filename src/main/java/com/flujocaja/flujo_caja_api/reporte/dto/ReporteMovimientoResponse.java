package com.flujocaja.flujo_caja_api.reporte.dto;

import java.util.List;

public record ReporteMovimientoResponse(

        ReporteMovimientoResumenResponse resumen,

        List<ReporteMovimientoDetalleResponse> movimientos

) {
}