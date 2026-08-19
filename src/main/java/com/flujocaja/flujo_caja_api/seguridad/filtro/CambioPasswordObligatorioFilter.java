package com.flujocaja.flujo_caja_api.seguridad.filtro;

import com.flujocaja.flujo_caja_api.seguridad.manejador.RespuestaSeguridadWriter;
import com.flujocaja.flujo_caja_api.seguridad.modelo.UsuarioAutenticado;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CambioPasswordObligatorioFilter
        extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(

            HttpServletRequest request,

            HttpServletResponse response,

            FilterChain filterChain

    ) throws ServletException, IOException {

        /* =====================================================
           PREFLIGHT CORS
           ===================================================== */

        if (
                "OPTIONS".equalsIgnoreCase(
                        request.getMethod()
                )
        ) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        /* =====================================================
           AUTHENTICATION
           ===================================================== */

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (
                authentication != null

                        &&

                        authentication.isAuthenticated()

                        &&

                        authentication.getPrincipal()
                                instanceof UsuarioAutenticado usuario

                        &&

                        Boolean.TRUE.equals(
                                usuario.debeCambiarPassword()
                        )
        ) {

            String uri =
                    request.getRequestURI();


            String metodo =
                    request.getMethod();


            /* =================================================
               ENDPOINTS PERMITIDOS DURANTE CAMBIO OBLIGATORIO
               ================================================= */

            boolean permitido =

                    (
                            "/api/cuenta".equals(uri)

                                    &&

                                    "GET".equalsIgnoreCase(
                                            metodo
                                    )
                    )

                            ||

                            (
                                    "/api/cuenta/password".equals(uri)

                                            &&

                                            "PATCH".equalsIgnoreCase(
                                                    metodo
                                            )
                            );


            if (!permitido) {

                RespuestaSeguridadWriter
                        .passwordChangeRequired(
                                response
                        );

                return;
            }
        }


        filterChain.doFilter(
                request,
                response
        );
    }
}