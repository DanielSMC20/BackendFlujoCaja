package com.flujocaja.flujo_caja_api.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCrearRequest(

        @NotBlank(
                message = "El correo es obligatorio."
        )
        @Email(
                message = "El correo no tiene un formato válido."
        )
        @Size(
                max = 254,
                message = "El correo no puede superar los 254 caracteres."
        )
        String correo,


        @NotBlank(
                message = "Los nombres son obligatorios."
        )
        @Size(
                max = 100,
                message = "Los nombres no pueden superar los 100 caracteres."
        )
        String nombres,


        @NotBlank(
                message = "Los apellidos son obligatorios."
        )
        @Size(
                max = 150,
                message = "Los apellidos no pueden superar los 150 caracteres."
        )
        String apellidos,


        @NotBlank(
                message = "La contraseña temporal es obligatoria."
        )
        @Size(
                min = 8,
                max = 72,
                message = "La contraseña debe tener entre 8 y 72 caracteres."
        )
        String passwordTemporal,


        @NotNull(
                message = "El rol es obligatorio."
        )
        Integer rolId

) {
}