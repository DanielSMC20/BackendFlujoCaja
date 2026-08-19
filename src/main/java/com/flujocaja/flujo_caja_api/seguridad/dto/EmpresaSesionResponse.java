package com.flujocaja.flujo_caja_api.seguridad.dto;

public record EmpresaSesionResponse(

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