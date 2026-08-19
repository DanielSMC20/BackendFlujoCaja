package com.flujocaja.flujo_caja_api.cargamasiva.dto;

public record CargaMasivaErrorResponse(

        Integer numeroRegistro,

        Integer filaExcel,

        String mensaje

) {
}