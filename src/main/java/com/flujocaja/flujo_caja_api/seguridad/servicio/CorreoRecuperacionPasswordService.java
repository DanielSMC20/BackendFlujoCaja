package com.flujocaja.flujo_caja_api.seguridad.servicio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoRecuperacionPasswordService {

    private final JavaMailSender mailSender;

    private final String frontendResetPasswordUrl;

    private final String mailFrom;

    private final String mailUsername;


    public CorreoRecuperacionPasswordService(
            JavaMailSender mailSender,

            @Value("${app.frontend.reset-password-url}")
            String frontendResetPasswordUrl,

            @Value("${app.mail.from:}")
            String mailFrom,

            @Value("${spring.mail.username:}")
            String mailUsername
    ) {

        this.mailSender = mailSender;

        this.frontendResetPasswordUrl =
                frontendResetPasswordUrl;

        this.mailFrom = mailFrom;

        this.mailUsername = mailUsername;
    }


    public void enviarEnlaceRecuperacion(
            String correoDestino,
            String nombreCompleto,
            String token,
            int expiracionMinutos
    ) {

        String separador =
                frontendResetPasswordUrl.contains("?")
                        ? "&"
                        : "?";


        String enlace =
                frontendResetPasswordUrl
                        + separador
                        + "token="
                        + token;


        String nombre =
                nombreCompleto == null
                        || nombreCompleto.isBlank()
                        ? "usuario"
                        : nombreCompleto.trim();


        SimpleMailMessage mensaje =
                new SimpleMailMessage();


        String remitente =
                mailFrom != null
                        && !mailFrom.isBlank()
                        ? mailFrom.trim()
                        : mailUsername;


        if (
                remitente != null
                        && !remitente.isBlank()
        ) {

            mensaje.setFrom(
                    remitente
            );
        }


        mensaje.setTo(
                correoDestino
        );


        mensaje.setSubject(
                "Restablece tu contraseña - Flujo Claro"
        );


        mensaje.setText(
                "Hola " + nombre + ",\n\n"
                        + "Recibimos una solicitud para restablecer "
                        + "la contraseña de tu cuenta en Flujo Claro.\n\n"

                        + "Ingresa al siguiente enlace:\n"
                        + enlace + "\n\n"

                        + "El enlace estará disponible durante "
                        + expiracionMinutos
                        + " minutos y solo podrá utilizarse una vez.\n\n"

                        + "Si no solicitaste este cambio, "
                        + "puedes ignorar este mensaje.\n\n"

                        + "Flujo Claro"
        );


        mailSender.send(
                mensaje
        );
    }
}