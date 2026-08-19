package com.flujocaja.flujo_caja_api.cargamasiva.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CargaMasivaFilaRequest(

        @NotNull
        Integer numeroRegistro,

        @NotNull
        Integer filaExcel,

        @NotNull(
                message = "La fecha es obligatoria."
        )
        LocalDate fecha,

        @NotBlank(
                message = "El dato es obligatorio."
        )
        @Size(max = 150)
        String dato,

        @NotNull(
                message = "El precio es obligatorio."
        )
        @DecimalMin(
                value = "0.01",
                message = "El precio debe ser mayor a cero."
        )
        BigDecimal precio,

        @NotBlank(
                message = "El clasificador es obligatorio."
        )
        @Size(max = 100)
        String clasificador,

        @NotNull(
                message = "El estado cancelado es obligatorio."
        )
        Boolean cancelado

) {
}