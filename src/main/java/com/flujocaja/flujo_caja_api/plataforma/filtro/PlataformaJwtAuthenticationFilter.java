package com.flujocaja.flujo_caja_api.plataforma.filtro;

import com.flujocaja.flujo_caja_api.plataforma.modelo.AdministradorPlataformaAutenticado;
import com.flujocaja.flujo_caja_api.plataforma.servicio.PlataformaJwtService;

import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class PlataformaJwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final PlataformaJwtService jwtService;


    public PlataformaJwtAuthenticationFilter(
            PlataformaJwtService jwtService
    ) {

        this.jwtService =
                jwtService;
    }


    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String uri =
                request.getRequestURI();


        return
                !uri.startsWith(
                        "/api/plataforma/"
                )

                        ||

                        "/api/plataforma/auth/login"
                                .equals(
                                        uri
                                );
    }


    @Override
    protected void doFilterInternal(

            HttpServletRequest request,

            HttpServletResponse response,

            FilterChain filterChain

    ) throws ServletException, IOException {

        String authorization =
                request.getHeader(
                        "Authorization"
                );


        if (
                authorization == null

                        ||

                        !authorization.startsWith(
                                "Bearer "
                        )
        ) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        String token =
                authorization
                        .substring(7)
                        .trim();


        try {

            Claims claims =
                    jwtService.obtenerClaims(
                            token
                    );


            if (
                    !jwtService.esTokenPlataforma(
                            claims
                    )
            ) {

                SecurityContextHolder
                        .clearContext();


                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }


            Long administradorId =
                    jwtService
                            .obtenerAdministradorId(
                                    claims
                            );


            String correo =
                    jwtService
                            .obtenerCorreo(
                                    claims
                            );


            if (
                    administradorId == null

                            ||

                            correo == null

                            ||

                            correo.isBlank()
            ) {

                SecurityContextHolder
                        .clearContext();


                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }


            var principal =
                    new AdministradorPlataformaAutenticado(

                            administradorId,

                            correo
                    );


            var authentication =
                    new UsernamePasswordAuthenticationToken(

                            principal,

                            null,

                            List.of(

                                    new SimpleGrantedAuthority(
                                            "ROLE_PLATAFORMA_ADMIN"
                                    )
                            )
                    );


            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            authentication
                    );

        } catch (Exception ex) {

            SecurityContextHolder
                    .clearContext();
        }


        filterChain.doFilter(
                request,
                response
        );
    }
}