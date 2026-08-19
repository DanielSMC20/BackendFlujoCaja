package com.flujocaja.flujo_caja_api.cuenta.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MiCuentaResponse(

        Long usuarioId,

        String correo,

        String nombres,

        String apellidos,

        String nombreCompleto,

        Boolean correoVerificado,

        Boolean debeCambiarPassword,

        LocalDateTime ultimoAcceso,

        EmpresaActualResponse empresa,

        List<String> roles

) {
}