package com.flujocaja.flujo_caja_api.movimiento.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoActualizarRequest(

        @NotNull
        Integer categoriaId,

        LocalDate fechaMovimiento,

        LocalDate fechaProyectada,

        @NotBlank
        @Size(max = 150)
        String descripcion,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal monto,

        Integer medioPago,

        Integer tipoComprobante,

        Integer moneda,

        @Size(max = 500)
        String observacion,

        LocalDate fechaComprobante,

        @Size(max = 20)
        String serieComprobante,

        @Size(max = 50)
        String numeroComprobante,

        @Size(max = 20)
        String documentoEmisor,

        @Size(max = 200)
        String razonSocialEmisor,

        @Size(max = 255)
        String archivoXmlNombre,

        @Pattern(
                regexp = "^[a-fA-F0-9]{64}$",
                message = "El hash del XML no es válido."
        )
        String hashXml

) {
}