package com.flujocaja.flujo_caja_api.flujo.controlador;

import com.flujocaja.flujo_caja_api.flujo.dto.FlujoCajaResponse;
import com.flujocaja.flujo_caja_api.flujo.servicio.FlujoCajaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/flujo-caja")
public class FlujoCajaController {

    private final FlujoCajaService flujoCajaService;


    public FlujoCajaController(
            FlujoCajaService flujoCajaService
    ) {

        this.flujoCajaService =
                flujoCajaService;
    }


    @GetMapping
    public ResponseEntity<FlujoCajaResponse> obtener(

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaDesde,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaHasta
    ) {

        return ResponseEntity.ok(

                flujoCajaService.obtener(
                        fechaDesde,
                        fechaHasta
                )
        );
    }
}