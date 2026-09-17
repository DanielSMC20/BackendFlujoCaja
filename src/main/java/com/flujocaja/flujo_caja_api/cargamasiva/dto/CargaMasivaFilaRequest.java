package com.flujocaja.flujo_caja_api.cargamasiva.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CargaMasivaFilaRequest(

        @NotNull
        @Positive
        Integer numeroRegistro,

        @NotNull
        @Positive
        Integer filaExcel,

        @NotNull
        LocalDate fechaMovimiento,

        @NotNull
        @Positive
        Integer categoriaId,

        @NotBlank
        @Size(max = 150)
        String descripcion,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal monto,

        @NotNull
        @Min(0)
        @Max(1)
        Integer bCancelado

) {
}