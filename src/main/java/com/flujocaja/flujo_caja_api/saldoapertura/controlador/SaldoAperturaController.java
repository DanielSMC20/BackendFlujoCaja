package com.flujocaja.flujo_caja_api.saldoapertura.controlador;

import com.flujocaja.flujo_caja_api.saldoapertura.dto.SaldoAperturaCrearRequest;
import com.flujocaja.flujo_caja_api.saldoapertura.dto.SaldoAperturaResponse;
import com.flujocaja.flujo_caja_api.saldoapertura.servicio.SaldoAperturaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saldo-apertura")
public class SaldoAperturaController {

    private final SaldoAperturaService saldoAperturaService;

    public SaldoAperturaController(
            SaldoAperturaService saldoAperturaService
    ) {

        this.saldoAperturaService =
                saldoAperturaService;
    }

    @GetMapping
    public ResponseEntity<SaldoAperturaResponse> obtener() {

        return ResponseEntity.ok(
                saldoAperturaService.obtener()
        );
    }

    @PostMapping
    public ResponseEntity<SaldoAperturaResponse> registrar(
            @Valid
            @RequestBody
            SaldoAperturaCrearRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        saldoAperturaService.registrar(request)
                );
    }
}
