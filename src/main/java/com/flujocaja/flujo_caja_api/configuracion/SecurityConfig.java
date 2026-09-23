package com.flujocaja.flujo_caja_api.configuracion;

import com.flujocaja.flujo_caja_api.seguridad.filtro.CambioPasswordObligatorioFilter;
import com.flujocaja.flujo_caja_api.seguridad.filtro.JwtAuthenticationFilter;
import com.flujocaja.flujo_caja_api.seguridad.manejador.RestAccessDeniedHandler;
import com.flujocaja.flujo_caja_api.seguridad.manejador.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CambioPasswordObligatorioFilter cambioPasswordObligatorioFilter;
    private final RestAuthenticationEntryPoint    restAuthenticationEntryPoint;

    private final RestAccessDeniedHandler   restAccessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CambioPasswordObligatorioFilter cambioPasswordObligatorioFilter,
             RestAuthenticationEntryPoint restAuthenticationEntryPoint,

            RestAccessDeniedHandler restAccessDeniedHandler

    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.cambioPasswordObligatorioFilter = cambioPasswordObligatorioFilter;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;

        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:4200}") String allowedOrigins
    ) {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.stream(allowedOrigins.split(","))
                        .map(String::trim)
                        .filter(origin -> !origin.isBlank())
                        .toList()
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept"
                )
        );

        configuration.setExposedHeaders(
                List.of(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "X-Access-Token"
                )
        );

        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/api/**",
                configuration
        );

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {

        return http

                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource)
                )

                .csrf(csrf ->
                        csrf.disable()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .exceptionHandling(exceptions ->
                        exceptions
                                .authenticationEntryPoint(
                                        restAuthenticationEntryPoint
                                )
                                .accessDeniedHandler(
                                        restAccessDeniedHandler
                                )
                )
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(

                                        "/api/auth/login",

                                        "/api/auth/forgot-password",

                                        "/api/auth/reset-password"

                                )
                                .permitAll()

                                .anyRequest()
                                .authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .addFilterAfter(
                        cambioPasswordObligatorioFilter,
                        JwtAuthenticationFilter.class
                )

                .build();
    }
}