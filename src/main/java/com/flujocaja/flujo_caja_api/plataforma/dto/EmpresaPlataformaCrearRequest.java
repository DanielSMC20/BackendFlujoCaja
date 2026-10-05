package com.flujocaja.flujo_caja_api.plataforma.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmpresaPlataformaCrearRequest(

        @NotBlank
        @Pattern(
                regexp = "\\d{11}",
                message =
                        "El RUC debe contener 11 dígitos."
        )
        String ruc,


        @NotBlank(
                message =
                        "La razón social es obligatoria."
        )
        @Size(max = 200)
        String razonSocial,


        @NotBlank(
                message =
                        "El nombre comercial es obligatorio."
        )
        @Size(max = 150)
        String nombreComercial,


        @NotNull(
                message =
                        "La moneda base es obligatoria."
        )
        Integer monedaBase,


        @NotBlank(
                message =
                        "La zona horaria es obligatoria."
        )
        @Size(max = 64)
        String zonaHoraria,


        @Valid
        @NotNull
        AdministradorInicialRequest administrador

) {
}