package com.flujocaja.flujo_caja_api.flujo.dto;

import java.util.List;

public record FlujoCajaResponse(

        FlujoCajaResumenResponse resumen,

        List<FlujoCajaDiarioResponse> detalle

) {
}