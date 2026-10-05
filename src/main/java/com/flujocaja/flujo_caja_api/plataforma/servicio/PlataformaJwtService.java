package com.flujocaja.flujo_caja_api.plataforma.servicio;

import com.flujocaja.flujo_caja_api.plataforma.modelo.AdministradorPlataformaAutenticado;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Date;

@Service
public class PlataformaJwtService {

    public static final String SCOPE =
            "PLATFORM_ADMIN";


    private static final String TIPO_TOKEN =
            "PLATAFORMA";


    private final String secret;

    private final long expirationMinutes;


    public PlataformaJwtService(

            @Value("${security.jwt.secret}")
            String secret,

            @Value("${security.jwt.plataforma-expiration-minutes:60}")
            long expirationMinutes

    ) {

        this.secret =
                secret;

        this.expirationMinutes =
                expirationMinutes;
    }


    public String generarToken(

            AdministradorPlataformaAutenticado administrador

    ) {

        Instant ahora =
                Instant.now();


        Instant expiracion =
                ahora.plusSeconds(
                        expiracionSegundos()
                );


        return Jwts
                .builder()

                .subject(
                        administrador.correo()
                )

                .claim(
                        "administradorPlataformaId",
                        administrador.id()
                )

                .claim(
                        "scope",
                        SCOPE
                )

                .claim(
                        "tipoToken",
                        TIPO_TOKEN
                )

                .issuedAt(
                        Date.from(
                                ahora
                        )
                )

                .expiration(
                        Date.from(
                                expiracion
                        )
                )

                .signWith(
                        obtenerClave()
                )

                .compact();
    }


    public Claims obtenerClaims(
            String token
    ) {

        return Jwts
                .parser()

                .verifyWith(
                        obtenerClave()
                )

                .build()

                .parseSignedClaims(
                        token
                )

                .getPayload();
    }


    public Long obtenerAdministradorId(
            Claims claims
    ) {

        Number valor =
                claims.get(
                        "administradorPlataformaId",
                        Number.class
                );


        return valor == null
                ? null
                : valor.longValue();
    }


    public String obtenerCorreo(
            Claims claims
    ) {

        return claims.getSubject();
    }


    public boolean esTokenPlataforma(
            Claims claims
    ) {

        return SCOPE.equals(

                claims.get(
                        "scope",
                        String.class
                )

        )

                &&

                TIPO_TOKEN.equals(

                        claims.get(
                                "tipoToken",
                                String.class
                        )
                );
    }


    public long expiracionSegundos() {

        return expirationMinutes
                *
                60L;
    }


    private SecretKey obtenerClave() {

        byte[] keyBytes =
                Decoders.BASE64.decode(
                        secret
                );


        return Keys.hmacShaKeyFor(
                keyBytes
        );
    }
}