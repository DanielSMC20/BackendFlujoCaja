package com.flujocaja.flujo_caja_api.comprobante.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ComprobanteXmlResponse(

        /* =====================================================
           ARCHIVO
           ===================================================== */

        String nombreArchivo,

        String hashXml,


        /* =====================================================
           FECHAS
           ===================================================== */

        LocalDate fechaEmision,

        LocalDate fechaVencimiento,


        /* =====================================================
           TIPO DE COMPROBANTE
           ===================================================== */

        Integer tipoComprobante,

        String codigoTipoComprobante,


        /* =====================================================
           SERIE Y NÚMERO
           ===================================================== */

        String serie,

        String numero,


        /* =====================================================
           MONEDA
           ===================================================== */

        Integer moneda,

        String codigoMoneda,


        /* =====================================================
           TOTAL
           ===================================================== */

        BigDecimal importeTotal,


        /* =====================================================
           DATOS TRIBUTARIOS
           ===================================================== */

        BigDecimal baseImponible,

        BigDecimal igv,

        BigDecimal inafecto,

        BigDecimal isc,

        BigDecimal icbper,

        BigDecimal exonerado,

        BigDecimal porcentajeIgv,

        BigDecimal tipoCambio,


        /* =====================================================
           EMISOR
           ===================================================== */

        String documentoEmisor,

        String razonSocialEmisor,


        /* =====================================================
           GLOSA
           ===================================================== */

        String descripcion,


        /* =====================================================
           CONTROL
           ===================================================== */

        Integer camposEncontrados

) {
}