package com.flujocaja.flujo_caja_api.plataforma.dto;

public record PlataformaLoginResponse(

        String accessToken,

        String tokenType,

        Long expiresIn,

        AdministradorPlataformaResponse administrador

) {
}