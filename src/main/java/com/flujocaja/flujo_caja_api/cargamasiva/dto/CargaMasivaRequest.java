package com.flujocaja.flujo_caja_api.cargamasiva.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CargaMasivaRequest(

        @NotBlank(
                message = "El nombre del archivo es obligatorio."
        )
        @Size(
                max = 255,
                message = "El nombre del archivo no puede superar los 255 caracteres."
        )
        String nombreArchivo,

        @NotEmpty(
                message = "La carga debe contener al menos un registro."
        )
        List<@Valid CargaMasivaFilaRequest> filas

) {
}