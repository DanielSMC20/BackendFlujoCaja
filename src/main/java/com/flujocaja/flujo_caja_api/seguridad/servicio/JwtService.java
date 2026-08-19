package com.flujocaja.flujo_caja_api.seguridad.servicio;

import com.flujocaja.flujo_caja_api.seguridad.modelo.UsuarioAutenticado;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final String secret;

    private final long expirationMinutes;


    public JwtService(

            @Value("${security.jwt.secret}")
            String secret,

            @Value("${security.jwt.expiration-minutes:15}")
            long expirationMinutes

    ) {

        this.secret =
                secret;

        this.expirationMinutes =
                expirationMinutes;
    }


    /* =========================================================
       GENERAR JWT
       ========================================================= */

    public String generarToken(
            UsuarioAutenticado usuario
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
                        usuario.correo()
                )

                .claim(
                        "usuarioId",
                        usuario.usuarioId()
                )

                .claim(
                        "empresaId",
                        usuario.empresaId()
                )

                .claim(
                        "roles",
                        usuario.roles()
                )

                .claim(
                        "debeCambiarPassword",
                        usuario.debeCambiarPassword()
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


    /* =========================================================
       OBTENER Y VALIDAR CLAIMS

       JJWT valida:
       - firma
       - estructura
       - expiración
       ========================================================= */

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


    /* =========================================================
       VALIDACIÓN SIMPLE
       ========================================================= */

    public boolean esValido(
            String token
    ) {

        try {

            Claims claims =
                    obtenerClaims(
                            token
                    );


            Date expiracion =
                    claims.getExpiration();


            return expiracion != null

                    &&

                    expiracion.after(
                            new Date()
                    );

        } catch (Exception ex) {

            return false;
        }
    }


    /* =========================================================
       USUARIO ID
       ========================================================= */

    public Long obtenerUsuarioId(
            Claims claims
    ) {

        Number valor =
                claims.get(
                        "usuarioId",
                        Number.class
                );


        return valor == null
                ? null
                : valor.longValue();
    }


    /* =========================================================
       EMPRESA ID
       ========================================================= */

    public Integer obtenerEmpresaId(
            Claims claims
    ) {

        Number valor =
                claims.get(
                        "empresaId",
                        Number.class
                );


        return valor == null
                ? null
                : valor.intValue();
    }


    /* =========================================================
       CORREO
       ========================================================= */

    public String obtenerCorreo(
            Claims claims
    ) {

        return claims.getSubject();
    }


    /* =========================================================
       ROLES
       ========================================================= */

    public List<String> obtenerRoles(
            Claims claims
    ) {

        Object valor =
                claims.get(
                        "roles"
                );


        if (!(valor instanceof List<?> lista)) {

            return List.of();
        }


        return lista
                .stream()

                .map(
                        String::valueOf
                )

                .toList();
    }


    /* =========================================================
       CAMBIO DE CONTRASEÑA
       ========================================================= */

    public boolean debeCambiarPassword(
            Claims claims
    ) {

        Boolean valor =
                claims.get(
                        "debeCambiarPassword",
                        Boolean.class
                );


        return Boolean.TRUE.equals(
                valor
        );
    }


    /* =========================================================
       EXPIRACIÓN
       ========================================================= */

    public long expiracionSegundos() {

        return expirationMinutes
                *
                60L;
    }


    /* =========================================================
       SECRET KEY
       ========================================================= */

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