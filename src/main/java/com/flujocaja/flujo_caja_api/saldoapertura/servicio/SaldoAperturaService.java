package com.flujocaja.flujo_caja_api.saldoapertura.servicio;

import com.flujocaja.flujo_caja_api.saldoapertura.dto.SaldoAperturaCrearRequest;
import com.flujocaja.flujo_caja_api.saldoapertura.dto.SaldoAperturaResponse;
import com.flujocaja.flujo_caja_api.saldoapertura.repositorio.SaldoAperturaRepository;
import com.flujocaja.flujo_caja_api.seguridad.servicio.ContextoSeguridad;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class SaldoAperturaService {

    private static final ZoneId ZONA_HORARIA_PERU =
            ZoneId.of("America/Lima");

    private final SaldoAperturaRepository saldoAperturaRepository;
    private final ContextoSeguridad contextoSeguridad;

    public SaldoAperturaService(
            SaldoAperturaRepository saldoAperturaRepository,
            ContextoSeguridad contextoSeguridad
    ) {

        this.saldoAperturaRepository =
                saldoAperturaRepository;

        this.contextoSeguridad =
                contextoSeguridad;
    }

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','CONTADOR','OPERADOR','CONSULTA')"
    )
    public SaldoAperturaResponse obtener() {

        return saldoAperturaRepository
                .obtener(
                        contextoSeguridad.empresaId()
                )
                .orElseGet(
                        SaldoAperturaResponse::sinConfigurar
                );
    }

    @PreAuthorize(
            "hasRole('ADMINISTRADOR')"
    )
    public SaldoAperturaResponse registrar(
            SaldoAperturaCrearRequest request
    ) {

        LocalDate fechaApertura =
                LocalDate.now(ZONA_HORARIA_PERU);

        return saldoAperturaRepository.registrar(
                contextoSeguridad.empresaId(),
                request.saldoInicial(),
                fechaApertura,
                contextoSeguridad.usuarioId()
        );
    }

    public SaldoAperturaResponse obtenerConfigurado() {

        SaldoAperturaResponse saldoApertura =
                obtener();

        if (!saldoApertura.configurado()) {

            throw new IllegalArgumentException(
                    "La empresa debe registrar su saldo de apertura."
            );
        }

        return saldoApertura;
    }
}
