package com.flujocaja.flujo_caja_api.reporte.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReporteMovimientoDetalleResponse(

        Long id,

        LocalDate fechaMovimiento,
        LocalDate fechaProyectada,
        LocalDate fechaPago,

        Integer tipoMovimiento,
        String tipoMovimientoDescripcion,

        Integer categoriaId,
        String categoria,

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

        Boolean cancelado,
        String estado,

        String observacion,

        Boolean activo,

        LocalDateTime fechaRegistro,
        LocalDateTime fechaModificacion,

        LocalDateTime fechaAnulacion,
        String motivoAnulacion

) {
}