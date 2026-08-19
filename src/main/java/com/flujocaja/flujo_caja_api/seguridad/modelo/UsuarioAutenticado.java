package com.flujocaja.flujo_caja_api.seguridad.modelo;

import java.util.List;

public record UsuarioAutenticado(

        Long usuarioId,

        Integer empresaId,

        String correo,

        List<String> roles,

        Boolean debeCambiarPassword

) {
}