package com.flujocaja.flujo_caja_api.plataforma.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdministradorInicialRequest(

        @NotBlank(
                message =
                        "Los nombres del administrador son obligatorios."
        )

        @Size(
                max = 100
        )

        String nombres,


        @NotBlank(
                message =
                        "Los apellidos del administrador son obligatorios."
        )

        @Size(
                max = 100
        )

        String apellidos,


        @NotBlank(
                message =
                        "El correo del administrador es obligatorio."
        )

        @Email(
                message =
                        "El correo no tiene un formato válido."
        )

        String correo,


        @NotBlank(
                message =
                        "La contraseña temporal es obligatoria."
        )

        @Size(
                min = 8,
                max = 72,

                message =
                        "La contraseña debe tener entre 8 y 72 caracteres."
        )

        String passwordTemporal

) {
}