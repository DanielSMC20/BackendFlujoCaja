package com.flujocaja.flujo_caja_api.seguridad.filtro;

import com.flujocaja.flujo_caja_api.seguridad.modelo.ContextoAutorizacion;
import com.flujocaja.flujo_caja_api.seguridad.modelo.UsuarioAutenticado;

import com.flujocaja.flujo_caja_api.seguridad.repositorio.AutorizacionRepository;

import com.flujocaja.flujo_caja_api.seguridad.servicio.JwtService;

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
import java.util.Optional;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    public static final String HEADER_TOKEN_RENOVADO =
            "X-Access-Token";


    private final JwtService jwtService;

    private final AutorizacionRepository autorizacionRepository;


    public JwtAuthenticationFilter(

            JwtService jwtService,

            AutorizacionRepository autorizacionRepository
    ) {

        this.jwtService =
                jwtService;

        this.autorizacionRepository =
                autorizacionRepository;
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


        /* =====================================================
           SIN BEARER
           ===================================================== */

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


        if (token.isEmpty()) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        try {

            /* =================================================
               1. VALIDAR JWT
               ================================================= */

            Claims claims =
                    jwtService.obtenerClaims(
                            token
                    );


            Long usuarioId =
                    jwtService.obtenerUsuarioId(
                            claims
                    );


            Integer empresaId =
                    jwtService.obtenerEmpresaId(
                            claims
                    );


            if (
                    usuarioId == null

                            ||

                            empresaId == null
            ) {

                SecurityContextHolder
                        .clearContext();


                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }


            /* =================================================
               2. OBTENER CONTEXTO ACTUAL

               No confiamos ciegamente en los roles
               almacenados en el JWT anterior.
               ================================================= */

            Optional<ContextoAutorizacion> contextoOptional =
                    autorizacionRepository.obtener(

                            usuarioId,

                            empresaId
                    );


            if (contextoOptional.isEmpty()) {

                SecurityContextHolder
                        .clearContext();


                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }


            ContextoAutorizacion contexto =
                    contextoOptional.get();


            /* =================================================
               3. PRINCIPAL ACTUAL
               ================================================= */

            UsuarioAutenticado usuario =
                    new UsuarioAutenticado(

                            contexto.usuarioId(),

                            contexto.empresaId(),

                            contexto.correo(),

                            contexto.roles(),

                            contexto.debeCambiarPassword()
                    );


            /* =================================================
               4. AUTHORITIES
               ================================================= */

            List<SimpleGrantedAuthority> authorities =
                    contexto
                            .roles()

                            .stream()

                            .map(
                                    rol ->
                                            new SimpleGrantedAuthority(
                                                    "ROLE_" + rol
                                            )
                            )

                            .toList();


            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(

                            usuario,

                            null,

                            authorities
                    );


            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            authentication
                    );



            String tokenRenovado =
                    jwtService.generarToken(
                            usuario
                    );


            response.setHeader(

                    HEADER_TOKEN_RENOVADO,

                    tokenRenovado
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