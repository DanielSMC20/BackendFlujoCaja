package com.flujocaja.flujo_caja_api.dashboard.controlador;

import com.flujocaja.flujo_caja_api.dashboard.dto.DashboardResponse;
import com.flujocaja.flujo_caja_api.dashboard.servicio.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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


    @GetMapping
    public ResponseEntity<DashboardResponse> obtener(

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
}