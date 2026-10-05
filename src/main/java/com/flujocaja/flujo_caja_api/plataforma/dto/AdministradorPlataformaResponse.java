package com.flujocaja.flujo_caja_api.plataforma.dto;

public record AdministradorPlataformaResponse(

        Long id,

        String correo,

        String nombres,

        String apellidos,

        String nombreCompleto

) {
}