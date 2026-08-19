package com.flujocaja.flujo_caja_api.movimiento.controlador;

import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoActualizarRequest;
import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoAnularRequest;
import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoCancelarRequest;
import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoCrearRequest;
import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoDetalleResponse;
import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoResponse;
import com.flujocaja.flujo_caja_api.movimiento.servicio.MovimientoService;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;


    public MovimientoController(
            MovimientoService movimientoService
    ) {

        this.movimientoService =
                movimientoService;
    }


    /* =========================================================
       LISTAR MOVIMIENTOS
       ========================================================= */

    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> listar(

            @RequestParam(required = false)
            Integer tipoMovimiento,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaDesde,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaHasta,

            @RequestParam(required = false)
            Boolean cancelado,

            @RequestParam(
                    defaultValue = "true"
            )
            Boolean soloActivos
    ) {

        return ResponseEntity.ok(

                movimientoService.listar(

                        tipoMovimiento,

                        fechaDesde,

                        fechaHasta,

                        cancelado,

                        soloActivos
                )
        );
    }


    /* =========================================================
       OBTENER MOVIMIENTO POR ID
       ========================================================= */

    @GetMapping("/{movimientoId}")
    public ResponseEntity<MovimientoDetalleResponse> obtenerPorId(

            @PathVariable
            Long movimientoId
    ) {

        return ResponseEntity.ok(

                movimientoService.obtenerPorId(
                        movimientoId
                )
        );
    }


    /* =========================================================
       REGISTRAR MOVIMIENTO
       ========================================================= */

    @PostMapping
    public ResponseEntity<MovimientoResponse> registrar(

            @Valid
            @RequestBody
            MovimientoCrearRequest request
    ) {

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(

                        movimientoService.registrar(
                                request
                        )
                );
    }


    /* =========================================================
       ACTUALIZAR MOVIMIENTO
       ========================================================= */

    @PutMapping("/{movimientoId}")
    public ResponseEntity<MovimientoResponse> actualizar(

            @PathVariable
            Long movimientoId,

            @Valid
            @RequestBody
            MovimientoActualizarRequest request
    ) {

        return ResponseEntity.ok(

                movimientoService.actualizar(

                        movimientoId,

                        request
                )
        );
    }


    /* =========================================================
       MARCAR EGRESO COMO PAGADO
       ========================================================= */

    @PatchMapping("/{movimientoId}/pagar")
    public ResponseEntity<MovimientoResponse> marcarComoPagado(

            @PathVariable
            Long movimientoId,

            @Valid
            @RequestBody
            MovimientoCancelarRequest request
    ) {

        return ResponseEntity.ok(

                movimientoService.marcarComoPagado(

                        movimientoId,

                        request
                )
        );
    }


    /* =========================================================
       ANULAR MOVIMIENTO
       ========================================================= */

    @PatchMapping("/{movimientoId}/anular")
    public ResponseEntity<MovimientoResponse> anular(

            @PathVariable
            Long movimientoId,

            @Valid
            @RequestBody
            MovimientoAnularRequest request
    ) {

        return ResponseEntity.ok(

                movimientoService.anular(

                        movimientoId,

                        request
                )
        );
    }
}