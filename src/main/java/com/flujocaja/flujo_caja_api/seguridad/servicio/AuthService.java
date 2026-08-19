package com.flujocaja.flujo_caja_api.seguridad.servicio;

import com.flujocaja.flujo_caja_api.seguridad.dto.EmpresaSesionResponse;
import com.flujocaja.flujo_caja_api.seguridad.dto.LoginRequest;
import com.flujocaja.flujo_caja_api.seguridad.dto.LoginResponse;
import com.flujocaja.flujo_caja_api.seguridad.dto.UsuarioSesionResponse;

import com.flujocaja.flujo_caja_api.seguridad.modelo.EmpresaLogin;
import com.flujocaja.flujo_caja_api.seguridad.modelo.UsuarioAutenticado;
import com.flujocaja.flujo_caja_api.seguridad.modelo.UsuarioLogin;

import com.flujocaja.flujo_caja_api.seguridad.repositorio.AuthRepository;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class AuthService {

    private final AuthRepository authRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    public AuthService(

            AuthRepository authRepository,

            PasswordEncoder passwordEncoder,

            JwtService jwtService

    ) {

        this.authRepository =
                authRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.jwtService =
                jwtService;
    }


    @Transactional
    public LoginResponse login(
            LoginRequest request
    ) {

        /* =====================================================
           NORMALIZAR CORREO
           ===================================================== */

        String correo =
                request.correo()
                        .trim()
                        .toLowerCase();


        /* =====================================================
           OBTENER USUARIO
           ===================================================== */

        UsuarioLogin usuario =
                authRepository
                        .buscarPorCorreo(
                                correo
                        )

                        .orElseThrow(

                                () ->
                                        new BadCredentialsException(
                                                "Correo o contraseña incorrectos."
                                        )
                        );


        /* =====================================================
           BLOQUEO
           ===================================================== */

        LocalDateTime ahoraUtc =
                LocalDateTime.now(
                        ZoneOffset.UTC
                );


        if (
                usuario.bloqueadoHasta() != null

                        &&

                        usuario.bloqueadoHasta()
                                .isAfter(
                                        ahoraUtc
                                )
        ) {

            throw new LockedException(
                    "La cuenta se encuentra temporalmente bloqueada."
            );
        }


        /* =====================================================
           CONTRASEÑA
           ===================================================== */

        boolean passwordCorrecta =
                passwordEncoder.matches(

                        request.password(),

                        usuario.passwordHash()
                );


        if (!passwordCorrecta) {

            authRepository
                    .registrarLoginFallido(
                            usuario.usuarioId()
                    );


            throw new BadCredentialsException(
                    "Correo o contraseña incorrectos."
            );
        }


        /* =====================================================
           EMPRESA

           Nuestro SaaS actual exige:
           exactamente una empresa activa.
           ===================================================== */

        if (
                usuario.cantidadEmpresasActivas() == null

                        ||

                        usuario.cantidadEmpresasActivas() != 1

                        ||

                        usuario.empresa() == null
        ) {

            throw new IllegalStateException(
                    "La cuenta no tiene una configuración de empresa válida."
            );
        }


        /* =====================================================
           ROLES
           ===================================================== */

        if (
                usuario.roles() == null

                        ||

                        usuario.roles().isEmpty()
        ) {

            throw new IllegalStateException(
                    "La cuenta no tiene un rol activo asignado."
            );
        }


        /* =====================================================
           LOGIN CORRECTO
           ===================================================== */

        authRepository
                .registrarLoginExitoso(
                        usuario.usuarioId()
                );


        /* =====================================================
           PRINCIPAL
           ===================================================== */

        UsuarioAutenticado autenticado =
                new UsuarioAutenticado(

                        usuario.usuarioId(),

                        usuario.empresa().id(),

                        usuario.correo(),

                        usuario.roles(),

                        usuario.debeCambiarPassword()
                );


        /* =====================================================
           JWT INICIAL
           ===================================================== */

        String accessToken =
                jwtService.generarToken(
                        autenticado
                );


        EmpresaLogin empresa =
                usuario.empresa();


        /* =====================================================
           RESPONSE
           ===================================================== */

        return new LoginResponse(

                accessToken,

                "Bearer",

                jwtService.expiracionSegundos(),

                new UsuarioSesionResponse(

                        usuario.usuarioId(),

                        usuario.correo(),

                        usuario.nombres(),

                        usuario.apellidos(),

                        usuario.nombreCompleto()
                ),

                new EmpresaSesionResponse(

                        empresa.id(),

                        empresa.ruc(),

                        empresa.razonSocial(),

                        empresa.nombreComercial(),

                        empresa.monedaBase(),

                        empresa.monedaBaseDescripcion(),

                        empresa.monedaBaseAbreviatura(),

                        empresa.zonaHoraria()
                ),

                usuario.roles(),

                usuario.debeCambiarPassword()
        );
    }
}