package com.flujocaja.flujo_caja_api.comprobante.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ComprobanteXmlResponse(

        String nombreArchivo,

        String hashXml,

        LocalDate fechaEmision,

        Integer tipoComprobante,

        String codigoTipoComprobante,

        String serie,

        String numero,

        Integer moneda,

        String codigoMoneda,

        BigDecimal importeTotal,

        String documentoEmisor,

        String razonSocialEmisor,

        String descripcion,

        Integer camposEncontrados

) {
}