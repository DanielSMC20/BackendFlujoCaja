package com.flujocaja.flujo_caja_api.plataforma.controlador;

import com.flujocaja.flujo_caja_api.plataforma.dto.PlataformaLoginRequest;
import com.flujocaja.flujo_caja_api.plataforma.dto.PlataformaLoginResponse;

import com.flujocaja.flujo_caja_api.plataforma.servicio.PlataformaAuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        "/api/plataforma/auth"
)
public class PlataformaAuthController {

    private final PlataformaAuthService authService;


    public PlataformaAuthController(
            PlataformaAuthService authService
    ) {

        this.authService =
                authService;
    }


    @PostMapping(
            "/login"
    )
    public ResponseEntity<PlataformaLoginResponse> login(

            @Valid
            @RequestBody
            PlataformaLoginRequest request
    ) {

        return ResponseEntity.ok(

                authService.login(
                        request
                )
        );
    }
}