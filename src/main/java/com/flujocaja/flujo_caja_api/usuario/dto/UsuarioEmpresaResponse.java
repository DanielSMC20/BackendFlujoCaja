package com.flujocaja.flujo_caja_api.usuario.dto;

import java.time.LocalDateTime;

public record UsuarioEmpresaResponse(

        Long id,

        String correo,

        String nombres,

        String apellidos,

        String nombreCompleto,

        Boolean activo,

        Integer rolId,

        String rolCodigo,

        String rolNombre,

        Boolean debeCambiarPassword,

        LocalDateTime ultimoAcceso,

        LocalDateTime fechaRegistro,

        LocalDateTime fechaBaja

) {
}