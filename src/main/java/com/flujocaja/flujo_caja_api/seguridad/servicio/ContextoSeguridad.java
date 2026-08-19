package com.flujocaja.flujo_caja_api.seguridad.servicio;

import com.flujocaja.flujo_caja_api.seguridad.modelo.UsuarioAutenticado;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContextoSeguridad {

    private UsuarioAutenticado obtenerPrincipal() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (
                authentication == null

                        ||

                        !authentication.isAuthenticated()

                        ||

                        !(
                                authentication.getPrincipal()
                                        instanceof UsuarioAutenticado usuario
                        )
        ) {

            throw new IllegalStateException(
                    "No existe un usuario autenticado."
            );
        }


        return usuario;
    }


    public Long usuarioId() {

        return obtenerPrincipal()
                .usuarioId();
    }


    public Integer empresaId() {

        return obtenerPrincipal()
                .empresaId();
    }


    public String correo() {

        return obtenerPrincipal()
                .correo();
    }


    public List<String> roles() {

        return obtenerPrincipal()
                .roles();
    }


    public boolean debeCambiarPassword() {

        return Boolean.TRUE.equals(

                obtenerPrincipal()
                        .debeCambiarPassword()
        );
    }
}