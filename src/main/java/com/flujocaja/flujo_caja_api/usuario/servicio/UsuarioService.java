package com.flujocaja.flujo_caja_api.usuario.servicio;

import com.flujocaja.flujo_caja_api.comun.excepcion.RecursoNoEncontradoException;
import com.flujocaja.flujo_caja_api.seguridad.servicio.ContextoSeguridad;

import com.flujocaja.flujo_caja_api.usuario.dto.*;
import com.flujocaja.flujo_caja_api.usuario.repositorio.UsuarioRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@PreAuthorize(
        "hasRole('ADMINISTRADOR')"
)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final ContextoSeguridad contextoSeguridad;

    private final PasswordEncoder passwordEncoder;


    public UsuarioService(
            UsuarioRepository usuarioRepository,
            ContextoSeguridad contextoSeguridad,
            PasswordEncoder passwordEncoder
    ) {

        this.usuarioRepository =
                usuarioRepository;

        this.contextoSeguridad =
                contextoSeguridad;

        this.passwordEncoder =
                passwordEncoder;
    }


    public List<UsuarioEmpresaResponse> listar(
            Boolean soloActivos
    ) {

        return usuarioRepository.listar(

                contextoSeguridad.empresaId(),

                Boolean.TRUE.equals(
                        soloActivos
                )
        );
    }


    public UsuarioEmpresaResponse obtener(
            Long usuarioId
    ) {

        return usuarioRepository
                .obtener(

                        contextoSeguridad.empresaId(),

                        usuarioId
                )
                .orElseThrow(

                        () ->
                                new RecursoNoEncontradoException(
                                        "El usuario no existe en esta empresa."
                                )
                );
    }


    public List<RolGestionResponse> listarRoles() {

        return usuarioRepository
                .listarRoles();
    }


    public UsuarioEmpresaResponse crear(
            UsuarioCrearRequest request
    ) {

        validarRolGestionable(
                request.rolId()
        );


        String passwordHash =
                passwordEncoder.encode(
                        request.passwordTemporal()
                );


        return usuarioRepository.crear(

                contextoSeguridad.empresaId(),

                contextoSeguridad.usuarioId(),

                request.correo()
                        .trim()
                        .toLowerCase(),

                request.nombres()
                        .trim(),

                request.apellidos()
                        .trim(),

                passwordHash,

                request.rolId()
        );
    }


    public UsuarioEmpresaResponse actualizar(
            Long usuarioId,
            UsuarioActualizarRequest request
    ) {

        if (
                usuarioId != null
                        &&
                        usuarioId.equals(
                                contextoSeguridad.usuarioId()
                        )
        ) {

            throw new IllegalArgumentException(
                    "No puedes modificar tu propio rol desde la administración de usuarios."
            );
        }


        validarRolGestionable(
                request.rolId()
        );


        return usuarioRepository.actualizar(

                contextoSeguridad.empresaId(),

                contextoSeguridad.usuarioId(),

                usuarioId,

                request.nombres()
                        .trim(),

                request.apellidos()
                        .trim(),

                request.rolId()
        );
    }

    public UsuarioEmpresaResponse cambiarEstado(
            Long usuarioId,
            UsuarioEstadoRequest request
    ) {

        if (
                usuarioId != null
                        &&
                        usuarioId.equals(
                                contextoSeguridad.usuarioId()
                        )
                        &&
                        Boolean.FALSE.equals(
                                request.activo()
                        )
        ) {

            throw new IllegalArgumentException(
                    "No puedes desactivar tu propia cuenta."
            );
        }


        return usuarioRepository.cambiarEstado(

                contextoSeguridad.empresaId(),

                contextoSeguridad.usuarioId(),

                usuarioId,

                request.activo()
        );
    }


    public UsuarioEmpresaResponse resetPassword(
            Long usuarioId,
            UsuarioResetPasswordRequest request
    ) {

        String passwordHash =
                passwordEncoder.encode(
                        request.nuevaPassword()
                );


        return usuarioRepository.resetPassword(

                contextoSeguridad.empresaId(),

                contextoSeguridad.usuarioId(),

                usuarioId,

                passwordHash
        );
    }


    private void validarRolGestionable(
            Integer rolId
    ) {

        if (
                rolId == null
                        ||
                        (
                                rolId != 2
                                        &&
                                        rolId != 3
                                        &&
                                        rolId != 4
                        )
        ) {

            throw new IllegalArgumentException(
                    "El rol seleccionado no puede asignarse desde este módulo."
            );
        }
    }
}