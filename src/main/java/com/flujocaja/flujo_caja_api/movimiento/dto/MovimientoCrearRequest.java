package com.flujocaja.flujo_caja_api.movimiento.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoCrearRequest(

        @NotNull
        Integer tipoMovimiento,

        @NotNull
        Integer categoriaId,

        LocalDate fechaMovimiento,

        LocalDate fechaProyectada,

        LocalDate fechaPago,

        Boolean cancelado,

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


        /* =========================================================
           COMPROBANTE
           ========================================================= */

        LocalDate fechaComprobante,

        LocalDate fechaVencimiento,

        @Size(max = 20)
        String serieComprobante,

        @Size(max = 50)
        String numeroComprobante,

        @Size(max = 20)
        String documentoEmisor,

        @Size(max = 200)
        String razonSocialEmisor,


        /* =========================================================
           DATOS TRIBUTARIOS DEL COMPROBANTE
           ========================================================= */

        @DecimalMin(value = "0.00")
        BigDecimal baseImponible,

        @DecimalMin(value = "0.00")
        BigDecimal igv,

        @DecimalMin(value = "0.00")
        BigDecimal inafecto,

        @DecimalMin(value = "0.00")
        BigDecimal isc,

        @DecimalMin(value = "0.00")
        BigDecimal icbper,

        @DecimalMin(value = "0.00")
        BigDecimal exonerado,

        @DecimalMin(value = "0.00")
        BigDecimal porcentajeIgv,

        @DecimalMin(value = "0.00")
        BigDecimal tipoCambio,


        /* =========================================================
           XML
           ========================================================= */

        @Size(max = 255)
        String archivoXmlNombre,

        @Pattern(
                regexp = "^[a-fA-F0-9]{64}$",
                message = "El hash del XML no es válido."
        )
        String hashXml

) {
}