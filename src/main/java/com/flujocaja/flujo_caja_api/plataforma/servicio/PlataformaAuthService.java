package com.flujocaja.flujo_caja_api.plataforma.servicio;

import com.flujocaja.flujo_caja_api.plataforma.dto.AdministradorPlataformaResponse;
import com.flujocaja.flujo_caja_api.plataforma.dto.PlataformaLoginRequest;
import com.flujocaja.flujo_caja_api.plataforma.dto.PlataformaLoginResponse;

import com.flujocaja.flujo_caja_api.plataforma.modelo.AdministradorPlataformaAutenticado;
import com.flujocaja.flujo_caja_api.plataforma.modelo.AdministradorPlataformaLogin;

import com.flujocaja.flujo_caja_api.plataforma.repositorio.PlataformaRepository;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class PlataformaAuthService {

    private final PlataformaRepository plataformaRepository;

    private final PlataformaJwtService jwtService;

    private final PasswordEncoder passwordEncoder;


    public PlataformaAuthService(

            PlataformaRepository plataformaRepository,

            PlataformaJwtService jwtService,

            PasswordEncoder passwordEncoder

    ) {

        this.plataformaRepository =
                plataformaRepository;

        this.jwtService =
                jwtService;

        this.passwordEncoder =
                passwordEncoder;
    }


    public PlataformaLoginResponse login(
            PlataformaLoginRequest request
    ) {

        String correo =
                request
                        .correo()
                        .trim()
                        .toLowerCase();


        AdministradorPlataformaLogin administrador =

                plataformaRepository

                        .buscarAdministradorPorCorreo(
                                correo
                        )

                        .orElseThrow(

                                () ->
                                        new BadCredentialsException(
                                                "Correo o contraseña incorrectos."
                                        )
                        );


        LocalDateTime ahora =
                LocalDateTime.now(
                        ZoneOffset.UTC
                );


        if (
                administrador.bloqueadoHasta() != null

                        &&

                        administrador
                                .bloqueadoHasta()
                                .isAfter(
                                        ahora
                                )
        ) {

            throw new BadCredentialsException(
                    "La cuenta se encuentra temporalmente bloqueada."
            );
        }


        if (
                !passwordEncoder.matches(

                        request.password(),

                        administrador.passwordHash()
                )
        ) {

            plataformaRepository
                    .registrarLoginFallido(
                            administrador.id()
                    );


            throw new BadCredentialsException(
                    "Correo o contraseña incorrectos."
            );
        }


        plataformaRepository
                .registrarLoginExitoso(
                        administrador.id()
                );


        AdministradorPlataformaAutenticado autenticado =

                new AdministradorPlataformaAutenticado(

                        administrador.id(),

                        administrador.correo()
                );


        String accessToken =
                jwtService.generarToken(
                        autenticado
                );


        return new PlataformaLoginResponse(

                accessToken,

                "Bearer",

                jwtService
                        .expiracionSegundos(),

                new AdministradorPlataformaResponse(

                        administrador.id(),

                        administrador.correo(),

                        administrador.nombres(),

                        administrador.apellidos(),

                        administrador.nombreCompleto()
                )
        );
    }
}