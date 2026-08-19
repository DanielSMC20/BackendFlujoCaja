package com.flujocaja.flujo_caja_api.seguridad.modelo;

public record EmpresaLogin(

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