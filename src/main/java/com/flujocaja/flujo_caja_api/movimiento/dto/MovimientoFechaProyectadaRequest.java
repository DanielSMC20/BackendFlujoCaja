package com.flujocaja.flujo_caja_api.movimiento.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MovimientoFechaProyectadaRequest(

        @NotNull
        LocalDate fechaProyectada

) {
}