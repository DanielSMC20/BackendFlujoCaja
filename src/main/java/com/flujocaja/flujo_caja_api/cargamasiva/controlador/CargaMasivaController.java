package com.flujocaja.flujo_caja_api.cargamasiva.controlador;

import com.flujocaja.flujo_caja_api.cargamasiva.dto.CargaMasivaRequest;
import com.flujocaja.flujo_caja_api.cargamasiva.dto.CargaMasivaResponse;
import com.flujocaja.flujo_caja_api.cargamasiva.servicio.CargaMasivaService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        "/api/cargas-masivas"
)
public class CargaMasivaController {

    private final CargaMasivaService cargaMasivaService;


    public CargaMasivaController(
            CargaMasivaService cargaMasivaService
    ) {

        this.cargaMasivaService =
                cargaMasivaService;
    }


    @PostMapping(
            "/egresos"
    )
    public ResponseEntity<CargaMasivaResponse> procesarEgresos(

            @Valid
            @RequestBody
            CargaMasivaRequest request
    ) {

        return ResponseEntity.ok(

                cargaMasivaService.procesar(
                        request
                )
        );
    }
}