package com.flujocaja.flujo_caja_api.seguridad.dto;

import java.util.List;

public record LoginResponse(

        String accessToken,

        String tokenType,

        Long expiresIn,

        UsuarioSesionResponse usuario,

        EmpresaSesionResponse empresa,

        List<String> roles,

        Boolean debeCambiarPassword

) {
}