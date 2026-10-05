package com.flujocaja.flujo_caja_api.plataforma.modelo;

import java.time.LocalDateTime;

public record AdministradorPlataformaLogin(

        Long id,

        String correo,

        String nombres,

        String apellidos,

        String passwordHash,

        Integer intentosFallidos,

        LocalDateTime bloqueadoHasta

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