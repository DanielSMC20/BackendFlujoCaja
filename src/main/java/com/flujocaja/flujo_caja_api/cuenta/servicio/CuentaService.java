package com.flujocaja.flujo_caja_api.cuenta.servicio;

import com.flujocaja.flujo_caja_api.cuenta.dto.CambiarPasswordRequest;
import com.flujocaja.flujo_caja_api.cuenta.dto.MiCuentaResponse;
import com.flujocaja.flujo_caja_api.cuenta.repositorio.CuentaRepository;

import com.flujocaja.flujo_caja_api.seguridad.servicio.ContextoSeguridad;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@PreAuthorize("isAuthenticated()")
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    private final ContextoSeguridad contextoSeguridad;

    private final PasswordEncoder passwordEncoder;


    public CuentaService(
            CuentaRepository cuentaRepository,
            ContextoSeguridad contextoSeguridad,
            PasswordEncoder passwordEncoder
    ) {

        this.cuentaRepository =
                cuentaRepository;

        this.contextoSeguridad =
                contextoSeguridad;

        this.passwordEncoder =
                passwordEncoder;
    }


    /* =========================================================
       MI CUENTA
       ========================================================= */

    public MiCuentaResponse obtenerMiCuenta() {

        return cuentaRepository.obtenerCuenta(

                contextoSeguridad.empresaId(),

                contextoSeguridad.usuarioId()
        );
    }


    /* =========================================================
       CAMBIAR PASSWORD
       ========================================================= */

    @Transactional
    public MiCuentaResponse cambiarPassword(
            CambiarPasswordRequest request
    ) {

        if (
                !request.nuevaPassword()
                        .equals(
                                request.confirmarPassword()
                        )
        ) {

            throw new IllegalArgumentException(
                    "La confirmación de la contraseña no coincide."
            );
        }


        if (
                request.passwordActual()
                        .equals(
                                request.nuevaPassword()
                        )
        ) {

            throw new IllegalArgumentException(
                    "La nueva contraseña debe ser diferente a la contraseña actual."
            );
        }


        Integer empresaId =
                contextoSeguridad.empresaId();

        Long usuarioId =
                contextoSeguridad.usuarioId();


        CuentaRepository.CredencialInterna credencial =
                cuentaRepository.obtenerCredencial(

                        empresaId,

                        usuarioId
                );


        /*
         * Nunca hacemos comparación de BCrypt
         * dentro de SQL Server.
         */

        boolean passwordCorrecta =
                passwordEncoder.matches(

                        request.passwordActual(),

                        credencial.passwordHash()
                );


        if (!passwordCorrecta) {

            throw new IllegalArgumentException(
                    "La contraseña actual no es correcta."
            );
        }


        /*
         * Evitamos volver a utilizar exactamente
         * la contraseña actual.
         */

        if (
                passwordEncoder.matches(

                        request.nuevaPassword(),

                        credencial.passwordHash()
                )
        ) {

            throw new IllegalArgumentException(
                    "La nueva contraseña debe ser diferente a la contraseña actual."
            );
        }


        String nuevoHash =
                passwordEncoder.encode(
                        request.nuevaPassword()
                );


        return cuentaRepository.cambiarPassword(

                empresaId,

                usuarioId,

                nuevoHash
        );
    }
}