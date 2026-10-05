package com.flujocaja.flujo_caja_api.plataforma.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmpresaPlataformaActualizarRequest(

        @NotBlank
        @Pattern(
                regexp = "\\d{11}",
                message =
                        "El RUC debe contener 11 dígitos."
        )
        String ruc,


        @NotBlank
        @Size(max = 200)
        String razonSocial,


        @NotBlank
        @Size(max = 150)
        String nombreComercial,


        @NotNull
        Integer monedaBase,


        @NotBlank
        @Size(max = 64)
        String zonaHoraria

) {
}