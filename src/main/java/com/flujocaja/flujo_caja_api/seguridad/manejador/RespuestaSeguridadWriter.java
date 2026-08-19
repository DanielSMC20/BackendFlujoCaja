package com.flujocaja.flujo_caja_api.seguridad.manejador;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class RespuestaSeguridadWriter {

    private RespuestaSeguridadWriter() {
    }


    /* =========================================================
       401
       ========================================================= */

    public static void unauthorized(
            HttpServletResponse response
    ) throws IOException {

        escribir(

                response,

                HttpServletResponse.SC_UNAUTHORIZED,

                """
                {
                  "codigo": "UNAUTHORIZED",
                  "mensaje": "La sesión no es válida o ha expirado."
                }
                """
        );
    }


    /* =========================================================
       403
       ========================================================= */

    public static void forbidden(
            HttpServletResponse response
    ) throws IOException {

        escribir(

                response,

                HttpServletResponse.SC_FORBIDDEN,

                """
                {
                  "codigo": "ACCESS_DENIED",
                  "mensaje": "No tiene permisos para realizar esta operación."
                }
                """
        );
    }


    /* =========================================================
       CAMBIO PASSWORD
       ========================================================= */

    public static void passwordChangeRequired(
            HttpServletResponse response
    ) throws IOException {

        escribir(

                response,

                HttpServletResponse.SC_FORBIDDEN,

                """
                {
                  "codigo": "PASSWORD_CHANGE_REQUIRED",
                  "mensaje": "Debe cambiar su contraseña antes de continuar."
                }
                """
        );
    }


    /* =========================================================
       RESPONSE
       ========================================================= */

    private static void escribir(

            HttpServletResponse response,

            int status,

            String body

    ) throws IOException {

        response.setStatus(
                status
        );

        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                "UTF-8"
        );

        response
                .getWriter()
                .write(
                        body
                );
    }
}