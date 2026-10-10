package com.flujocaja.flujo_caja_api.saldoapertura.servicio;

import com.flujocaja.flujo_caja_api.saldoapertura.dto.SaldoAperturaCrearRequest;
import com.flujocaja.flujo_caja_api.saldoapertura.dto.SaldoAperturaResponse;
import com.flujocaja.flujo_caja_api.saldoapertura.repositorio.SaldoAperturaRepository;
import com.flujocaja.flujo_caja_api.seguridad.servicio.ContextoSeguridad;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class SaldoAperturaService {

    private final SaldoAperturaRepository saldoAperturaRepository;
    private final ContextoSeguridad contextoSeguridad;

    public SaldoAperturaService(
            SaldoAperturaRepository saldoAperturaRepository,
            ContextoSeguridad contextoSeguridad
    ) {
        this.saldoAperturaRepository = saldoAperturaRepository;
        this.contextoSeguridad = contextoSeguridad;
    }

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','CONTADOR','OPERADOR','CONSULTA')"
    )
    public SaldoAperturaResponse obtener() {

        return saldoAperturaRepository
                .obtener(contextoSeguridad.empresaId())
                .orElseGet(SaldoAperturaResponse::sinConfigurar);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public SaldoAperturaResponse registrar(
            SaldoAperturaCrearRequest request
    ) {

        Integer empresaId = contextoSeguridad.empresaId();

        boolean yaExiste = saldoAperturaRepository
                .obtener(empresaId)
                .map(SaldoAperturaResponse::configurado)
                .orElse(false);

        if (yaExiste) {
            throw new IllegalArgumentException(
                    "La empresa ya tiene registrado un saldo de apertura."
            );
        }

        return saldoAperturaRepository.registrar(
                empresaId,
                request.saldoInicial(),
                request.fechaApertura(),
                contextoSeguridad.usuarioId()
        );
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public SaldoAperturaResponse editar(
            SaldoAperturaCrearRequest request
    ) {

        Integer empresaId = contextoSeguridad.empresaId();

        boolean yaConfigurado = saldoAperturaRepository
                .obtener(empresaId)
                .map(SaldoAperturaResponse::configurado)
                .orElse(false);

        if (!yaConfigurado) {
            throw new IllegalArgumentException(
                    "La empresa todavía no tiene un saldo de apertura. Debe registrarlo primero."
            );
        }

        return saldoAperturaRepository.editar(
                empresaId,
                request.saldoInicial(),
                request.fechaApertura(),
                contextoSeguridad.usuarioId()
        );
    }

    public SaldoAperturaResponse obtenerConfigurado() {

        SaldoAperturaResponse saldoApertura = obtener();

        if (!saldoApertura.configurado()) {
            throw new IllegalArgumentException(
                    "La empresa debe registrar su saldo de apertura."
            );
        }

        return saldoApertura;
    }
}