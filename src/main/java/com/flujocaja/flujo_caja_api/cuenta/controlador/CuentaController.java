package com.flujocaja.flujo_caja_api.cuenta.controlador;

import com.flujocaja.flujo_caja_api.cuenta.dto.CambiarPasswordRequest;
import com.flujocaja.flujo_caja_api.cuenta.dto.MiCuentaResponse;
import com.flujocaja.flujo_caja_api.cuenta.servicio.CuentaService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        "/api/cuenta"
)
public class CuentaController {

    private final CuentaService cuentaService;


    public CuentaController(
            CuentaService cuentaService
    ) {

        this.cuentaService =
                cuentaService;
    }


    /* =========================================================
       MI CUENTA
       ========================================================= */

    @GetMapping
    public ResponseEntity<MiCuentaResponse> obtenerMiCuenta() {

        return ResponseEntity.ok(

                cuentaService.obtenerMiCuenta()
        );
    }


    /* =========================================================
       CAMBIAR PASSWORD
       ========================================================= */

    @PatchMapping("/password")
    public ResponseEntity<MiCuentaResponse> cambiarPassword(

            @Valid
            @RequestBody
            CambiarPasswordRequest request
    ) {

        return ResponseEntity.ok(

                cuentaService.cambiarPassword(
                        request
                )
        );
    }
}