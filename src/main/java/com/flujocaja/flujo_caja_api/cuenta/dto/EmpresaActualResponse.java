package com.flujocaja.flujo_caja_api.cuenta.dto;

public record EmpresaActualResponse(

        Integer id,

        String ruc,

        String razonSocial,

        String nombreComercial,

        Integer monedaBase,

        String monedaBaseDescripcion,

        String monedaBaseAbreviatura,

        String zonaHoraria

) {
}