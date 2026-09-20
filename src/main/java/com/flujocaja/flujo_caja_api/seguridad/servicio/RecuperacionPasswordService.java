package com.flujocaja.flujo_caja_api.seguridad.servicio;

import com.flujocaja.flujo_caja_api.seguridad.dto.MensajeAuthResponse;
import com.flujocaja.flujo_caja_api.seguridad.dto.RestablecerPasswordRequest;
import com.flujocaja.flujo_caja_api.seguridad.dto.SolicitarRecuperacionPasswordRequest;

import com.flujocaja.flujo_caja_api.seguridad.repositorio.RecuperacionPasswordRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RecuperacionPasswordService {

    private static final String MENSAJE_SOLICITUD =
            "Si el correo se encuentra registrado, recibirás las instrucciones para restablecer tu contraseña.";

    private static final String MENSAJE_RESTABLECIDO =
            "Tu contraseña fue restablecida correctamente. Ya puedes iniciar sesión.";


    private final RecuperacionPasswordRepository recuperacionPasswordRepository;

    private final CorreoRecuperacionPasswordService correoService;

    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom =
            new SecureRandom();

    private final int expiracionMinutos;


    public RecuperacionPasswordService(
            RecuperacionPasswordRepository recuperacionPasswordRepository,

            CorreoRecuperacionPasswordService correoService,

            PasswordEncoder passwordEncoder,

            @Value("${app.password-reset.expiration-minutes:20}")
            int expiracionMinutos
    ) {

        this.recuperacionPasswordRepository =
                recuperacionPasswordRepository;

        this.correoService =
                correoService;

        this.passwordEncoder =
                passwordEncoder;

        this.expiracionMinutos =
                expiracionMinutos;
    }


    public MensajeAuthResponse solicitarRecuperacion(
            SolicitarRecuperacionPasswordRequest request
    ) {

        String correo =
                request.correo()
                        .trim()
                        .toLowerCase();


        String token =
                generarTokenSeguro();


        String tokenHash =
                calcularSha256(
                        token
                );


        recuperacionPasswordRepository
                .crearToken(
                        correo,
                        tokenHash,
                        expiracionMinutos
                )
                .ifPresent(
                        usuario ->
                                correoService
                                        .enviarEnlaceRecuperacion(
                                                usuario.correo(),
                                                usuario.nombreCompleto(),
                                                token,
                                                expiracionMinutos
                                        )
                );


        return new MensajeAuthResponse(
                MENSAJE_SOLICITUD
        );
    }


    public MensajeAuthResponse restablecerPassword(
            RestablecerPasswordRequest request
    ) {

        if (
                !request.nuevaPassword()
                        .equals(
                                request.confirmarPassword()
                        )
        ) {

            throw new IllegalArgumentException(
                    "La confirmación de la contraseña no coincide."
            );
        }


        String token =
                request.token()
                        .trim();


        if (
                token.length() < 32
        ) {

            throw new IllegalArgumentException(
                    "El enlace de recuperación no es válido o ha expirado."
            );
        }


        String tokenHash =
                calcularSha256(
                        token
                );


        String passwordHash =
                passwordEncoder.encode(
                        request.nuevaPassword()
                );


        recuperacionPasswordRepository
                .consumirToken(
                        tokenHash,
                        passwordHash
                );


        return new MensajeAuthResponse(
                MENSAJE_RESTABLECIDO
        );
    }


    private String generarTokenSeguro() {

        byte[] bytes =
                new byte[32];


        secureRandom.nextBytes(
                bytes
        );


        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        bytes
                );
    }


    private String calcularSha256(
            String valor
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );


            byte[] hash =
                    digest.digest(
                            valor.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );


            return HexFormat
                    .of()
                    .formatHex(
                            hash
                    );

        } catch (
                NoSuchAlgorithmException ex
        ) {

            throw new IllegalStateException(
                    "No se pudo procesar el token de recuperación.",
                    ex
            );
        }
    }
}