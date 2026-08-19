package com.flujocaja.flujo_caja_api.comun.dto;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(

        Instant timestamp,

        int status,

        String error,

        String message,

        String path,

        Map<String, String> campos

) {
}