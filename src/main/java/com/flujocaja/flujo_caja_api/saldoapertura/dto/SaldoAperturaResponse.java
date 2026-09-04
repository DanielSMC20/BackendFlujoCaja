package com.flujocaja.flujo_caja_api.saldoapertura.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SaldoAperturaResponse(

        boolean configurado,

        BigDecimal saldoInicial,
        LocalDate fechaApertura,

        Integer moneda,
        String monedaDescripcion,

        Long usuarioRegistroId,
        LocalDateTime fechaRegistro

) {

    public static SaldoAperturaResponse sinConfigurar() {

        return new SaldoAperturaResponse(
                false,
                null,
                null,
                1,
                "Soles",
                null,
                null
        );
    }
}
