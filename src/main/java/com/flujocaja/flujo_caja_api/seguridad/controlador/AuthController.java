package com.flujocaja.flujo_caja_api.seguridad.controlador;

import com.flujocaja.flujo_caja_api.seguridad.dto.LoginRequest;
import com.flujocaja.flujo_caja_api.seguridad.dto.LoginResponse;

import com.flujocaja.flujo_caja_api.seguridad.servicio.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import com.flujocaja.flujo_caja_api.seguridad.dto.MensajeAuthResponse;
import com.flujocaja.flujo_caja_api.seguridad.dto.RestablecerPasswordRequest;
import com.flujocaja.flujo_caja_api.seguridad.dto.SolicitarRecuperacionPasswordRequest;

import com.flujocaja.flujo_caja_api.seguridad.servicio.RecuperacionPasswordService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RecuperacionPasswordService recuperacionPasswordService;
    private final AuthService authService;

    public AuthController(

            AuthService authService,

            RecuperacionPasswordService recuperacionPasswordService

    ) {

        this.authService =
                authService;

        this.recuperacionPasswordService =
                recuperacionPasswordService;
    }



    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(

            @Valid
            @RequestBody
            LoginRequest request
    ) {

        return ResponseEntity.ok(

                authService.login(
                        request
                )
        );
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<MensajeAuthResponse> solicitarRecuperacionPassword(

            @Valid
            @RequestBody
            SolicitarRecuperacionPasswordRequest request

    ) {

        return ResponseEntity.ok(

                recuperacionPasswordService
                        .solicitarRecuperacion(
                                request
                        )
        );
    }
    @PostMapping("/reset-password")
    public ResponseEntity<MensajeAuthResponse> restablecerPassword(

            @Valid
            @RequestBody
            RestablecerPasswordRequest request

    ) {

        return ResponseEntity.ok(

                recuperacionPasswordService
                        .restablecerPassword(
                                request
                        )
        );
    }
}