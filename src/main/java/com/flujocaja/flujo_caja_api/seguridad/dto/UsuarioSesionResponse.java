package com.flujocaja.flujo_caja_api.seguridad.dto;

public record UsuarioSesionResponse(

        Long id,

        String correo,

        String nombres,

        String apellidos,

        String nombreCompleto

) {
}