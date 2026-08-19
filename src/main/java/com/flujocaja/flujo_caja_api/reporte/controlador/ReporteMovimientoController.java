package com.flujocaja.flujo_caja_api.reporte.controlador;

import com.flujocaja.flujo_caja_api.reporte.dto.ReporteMovimientoResponse;
import com.flujocaja.flujo_caja_api.reporte.servicio.ReporteMovimientoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes/movimientos")
public class ReporteMovimientoController {

    private final ReporteMovimientoService reporteMovimientoService;


    public ReporteMovimientoController(
            ReporteMovimientoService reporteMovimientoService
    ) {

        this.reporteMovimientoService =
                reporteMovimientoService;
    }


    @GetMapping
    public ResponseEntity<ReporteMovimientoResponse> obtener(

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
            Integer tipoMovimiento,

            @RequestParam(required = false)
            Integer categoriaId,

            @RequestParam(required = false)
            Boolean cancelado,

            @RequestParam(required = false)
            Integer origenRegistro,

            @RequestParam(defaultValue = "false")
            Boolean incluirAnulados
    ) {

        return ResponseEntity.ok(

                reporteMovimientoService.obtener(
                        fechaDesde,
                        fechaHasta,
                        tipoMovimiento,
                        categoriaId,
                        cancelado,
                        origenRegistro,
                        incluirAnulados
                )
        );
    }
}