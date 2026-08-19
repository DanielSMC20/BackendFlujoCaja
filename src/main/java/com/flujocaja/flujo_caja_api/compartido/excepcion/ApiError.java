package com.flujocaja.flujo_caja_api.compartido.excepcion;

import java.time.Instant;

public record ApiError(
        Instant fecha,
        int estado,
        String mensaje
) {
}