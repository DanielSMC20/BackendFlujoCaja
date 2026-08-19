package com.flujocaja.flujo_caja_api.movimiento.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoDetalleResponse(

        Long id,

        Integer tipoMovimiento,
        String tipoMovimientoDescripcion,

        Integer categoriaId,
        String categoria,

        LocalDate fechaMovimiento,
        LocalDate fechaProyectada,
        LocalDate fechaPago,

        Boolean cancelado,

        String descripcion,
        BigDecimal monto,

        Integer medioPago,
        String medioPagoDescripcion,

        Integer tipoComprobante,
        String tipoComprobanteDescripcion,

        Integer moneda,
        String monedaDescripcion,
        String monedaAbreviatura,

        Integer origenRegistro,
        String origenRegistroDescripcion,

        String observacion,

        Boolean activo,

        LocalDate fechaComprobante,

        String serieComprobante,

        String numeroComprobante,

        String documentoEmisor,

        String razonSocialEmisor,

        String archivoXmlNombre,

        String hashXml

) {
}