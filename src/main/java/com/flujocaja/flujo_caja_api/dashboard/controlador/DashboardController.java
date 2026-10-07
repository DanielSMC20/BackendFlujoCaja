package com.flujocaja.flujo_caja_api.dashboard.controlador;

import com.flujocaja.flujo_caja_api.dashboard.dto.DashboardResponse;
import com.flujocaja.flujo_caja_api.dashboard.dto.DashboardResumenAnualResponse;

import com.flujocaja.flujo_caja_api.dashboard.servicio.DashboardService;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;


    public DashboardController(
            DashboardService dashboardService
    ) {

        this.dashboardService =
                dashboardService;
    }


    /* =========================================================
       DASHBOARD ACTUAL
       ========================================================= */

    @GetMapping
    public ResponseEntity<DashboardResponse> obtener(

            @RequestParam(
                    required = false
            )
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaDesde,


            @RequestParam(
                    required = false
            )
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaHasta,


            @RequestParam(
                    defaultValue = "10"
            )
            Integer cantidadUltimos
    ) {

        return ResponseEntity.ok(

                dashboardService.obtener(
                        fechaDesde,
                        fechaHasta,
                        cantidadUltimos
                )
        );
    }


    /* =========================================================
       NUEVO RESUMEN ÚLTIMOS 3 AÑOS
       ========================================================= */

    @GetMapping(
            "/resumen-anual"
    )
    public ResponseEntity<List<DashboardResumenAnualResponse>>
    obtenerResumenAnual(

            @RequestParam(
                    "anio"
            )
            Integer anioActual
    ) {

        return ResponseEntity.ok(

                dashboardService.obtenerResumenAnual(
                        anioActual
                )
        );
    }
}