package com.flujocaja.flujo_caja_api.seguridad.controlador;

import com.flujocaja.flujo_caja_api.seguridad.dto.LoginRequest;
import com.flujocaja.flujo_caja_api.seguridad.dto.LoginResponse;

import com.flujocaja.flujo_caja_api.seguridad.servicio.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(
            AuthService authService
    ) {

        this.authService =
                authService;
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
}