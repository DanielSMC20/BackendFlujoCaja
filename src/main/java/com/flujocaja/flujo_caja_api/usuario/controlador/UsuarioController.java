package com.flujocaja.flujo_caja_api.usuario.controlador;

import com.flujocaja.flujo_caja_api.usuario.dto.*;
import com.flujocaja.flujo_caja_api.usuario.servicio.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/usuarios"
)
public class UsuarioController {

    private final UsuarioService usuarioService;


    public UsuarioController(
            UsuarioService usuarioService
    ) {

        this.usuarioService =
                usuarioService;
    }


    /* =========================================================
       LISTAR
       ========================================================= */

    @GetMapping
    public ResponseEntity<List<UsuarioEmpresaResponse>> listar(

            @RequestParam(
                    defaultValue = "false"
            )
            Boolean soloActivos
    ) {

        return ResponseEntity.ok(

                usuarioService.listar(
                        soloActivos
                )
        );
    }


    /* =========================================================
       ROLES
       ========================================================= */

    @GetMapping("/roles")
    public ResponseEntity<List<RolGestionResponse>> listarRoles() {

        return ResponseEntity.ok(

                usuarioService.listarRoles()
        );
    }


    /* =========================================================
       OBTENER
       ========================================================= */

    @GetMapping("/{usuarioId}")
    public ResponseEntity<UsuarioEmpresaResponse> obtener(

            @PathVariable
            Long usuarioId
    ) {

        return ResponseEntity.ok(

                usuarioService.obtener(
                        usuarioId
                )
        );
    }


    /* =========================================================
       CREAR
       ========================================================= */

    @PostMapping
    public ResponseEntity<UsuarioEmpresaResponse> crear(

            @Valid
            @RequestBody
            UsuarioCrearRequest request
    ) {

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(

                        usuarioService.crear(
                                request
                        )
                );
    }


    /* =========================================================
       ACTUALIZAR
       ========================================================= */

    @PutMapping("/{usuarioId}")
    public ResponseEntity<UsuarioEmpresaResponse> actualizar(

            @PathVariable
            Long usuarioId,

            @Valid
            @RequestBody
            UsuarioActualizarRequest request
    ) {

        return ResponseEntity.ok(

                usuarioService.actualizar(

                        usuarioId,

                        request
                )
        );
    }


    /* =========================================================
       ACTIVAR / DESACTIVAR
       ========================================================= */

    @PatchMapping("/{usuarioId}/estado")
    public ResponseEntity<UsuarioEmpresaResponse> cambiarEstado(

            @PathVariable
            Long usuarioId,

            @Valid
            @RequestBody
            UsuarioEstadoRequest request
    ) {

        return ResponseEntity.ok(

                usuarioService.cambiarEstado(

                        usuarioId,

                        request
                )
        );
    }


    /* =========================================================
       RESET PASSWORD
       ========================================================= */

    @PatchMapping("/{usuarioId}/password")
    public ResponseEntity<UsuarioEmpresaResponse> resetPassword(

            @PathVariable
            Long usuarioId,

            @Valid
            @RequestBody
            UsuarioResetPasswordRequest request
    ) {

        return ResponseEntity.ok(

                usuarioService.resetPassword(

                        usuarioId,

                        request
                )
        );
    }
}