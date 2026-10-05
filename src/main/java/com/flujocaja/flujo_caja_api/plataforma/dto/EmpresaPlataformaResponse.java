package com.flujocaja.flujo_caja_api.plataforma.dto;

import java.time.LocalDateTime;

public record EmpresaPlataformaResponse(

        Integer empresaId,

        String ruc,

        String razonSocial,

        String nombreComercial,

        Integer monedaBase,

        String monedaDescripcion,

        String monedaAbreviatura,

        String zonaHoraria,

        Boolean activa,

        LocalDateTime fechaRegistro,


        Long administradorUsuarioId,

        String administradorNombres,

        String administradorApellidos,

        String administradorNombreCompleto,

        String administradorCorreo

) {
}