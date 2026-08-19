package com.flujocaja.flujo_caja_api.dashboard.dto;

import java.util.List;

public record DashboardResponse(

        DashboardResumenResponse resumen,

        List<FlujoDiarioResponse> flujoDiario,

        List<UltimoMovimientoResponse> ultimosMovimientos

) {
}