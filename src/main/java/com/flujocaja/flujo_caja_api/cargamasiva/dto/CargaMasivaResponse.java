package com.flujocaja.flujo_caja_api.cargamasiva.dto;

import java.math.BigDecimal;
import java.util.List;

public record CargaMasivaResponse(

        Long cargaMasivaId,

        Integer estadoCarga,

        String estadoCargaDescripcion,

        Integer totalFilas,

        Integer filasValidas,

        Integer filasError,

        Integer totalProyectados,

        Integer totalCancelados,

        BigDecimal montoProyectado,

        BigDecimal montoCancelado,

        BigDecimal montoTotal,

        List<CargaMasivaErrorResponse> errores

) {
}