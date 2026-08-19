package com.flujocaja.flujo_caja_api.seguridad.modelo;

import java.time.LocalDateTime;
import java.util.List;

public record UsuarioLogin(

        Long usuarioId,

        String correo,

        String nombres,

        String apellidos,

        String passwordHash,

        Integer intentosFallidos,

        LocalDateTime bloqueadoHasta,

        Boolean debeCambiarPassword,

        Boolean correoVerificado,

        Integer cantidadEmpresasActivas,

        EmpresaLogin empresa,

        List<String> roles

) {

    public String nombreCompleto() {

        return (
                nombres
                        +
                        " "
                        +
                        apellidos
        ).trim();
    }
}