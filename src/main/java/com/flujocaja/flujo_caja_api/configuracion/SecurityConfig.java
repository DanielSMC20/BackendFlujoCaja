package com.flujocaja.flujo_caja_api.configuracion;

import com.flujocaja.flujo_caja_api.seguridad.filtro.CambioPasswordObligatorioFilter;
import com.flujocaja.flujo_caja_api.seguridad.filtro.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CambioPasswordObligatorioFilter cambioPasswordObligatorioFilter;


    public SecurityConfig(

            JwtAuthenticationFilter jwtAuthenticationFilter,

            CambioPasswordObligatorioFilter cambioPasswordObligatorioFilter

    ) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;

        this.cambioPasswordObligatorioFilter =
                cambioPasswordObligatorioFilter;
    }


    /* =========================================================
       PASSWORD ENCODER
       ========================================================= */

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    /* =========================================================
       SECURITY FILTER CHAIN
       ========================================================= */

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        return http

                /* =================================================
                   API REST STATELESS
                   ================================================= */

                .csrf(
                        csrf ->
                                csrf.disable()
                )


                /* =================================================
                   NO HttpSession
                   ================================================= */

                .sessionManagement(

                        session ->
                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )


                /* =================================================
                   ENDPOINTS
                   ================================================= */

                .authorizeHttpRequests(

                        auth ->
                                auth

                                        .requestMatchers(
                                                "/api/auth/login"
                                        )
                                        .permitAll()

                                        .anyRequest()
                                        .authenticated()
                )


                /* =================================================
                   1. JWT
                   ================================================= */

                .addFilterBefore(

                        jwtAuthenticationFilter,

                        UsernamePasswordAuthenticationFilter.class
                )


                /* =================================================
                   2. CAMBIO PASSWORD OBLIGATORIO
                   ================================================= */

                .addFilterAfter(

                        cambioPasswordObligatorioFilter,

                        JwtAuthenticationFilter.class
                )


                .build();
    }
}