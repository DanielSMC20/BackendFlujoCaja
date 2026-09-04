package com.flujocaja.flujo_caja_api.cargamasiva.controlador;

import com.flujocaja.flujo_caja_api.cargamasiva.dto.CargaMasivaRequest;
import com.flujocaja.flujo_caja_api.cargamasiva.dto.CargaMasivaResponse;
import com.flujocaja.flujo_caja_api.cargamasiva.servicio.CargaMasivaService;

import com.flujocaja.flujo_caja_api.cargamasiva.servicio.PlantillaCargaMasivaService;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        "/api/cargas-masivas"
)
public class CargaMasivaController {

    private final CargaMasivaService cargaMasivaService;
    private final PlantillaCargaMasivaService plantillaCargaMasivaService;


    private static final String NOMBRE_PLANTILLA =
            "plantilla-carga-masiva-egresos.xlsx";

    private static final MediaType TIPO_CONTENIDO_EXCEL =
            MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );
    public CargaMasivaController(
            CargaMasivaService cargaMasivaService,
            PlantillaCargaMasivaService plantillaCargaMasivaService) {

        this.cargaMasivaService =
                cargaMasivaService;
        this.plantillaCargaMasivaService = plantillaCargaMasivaService;
    }

    @GetMapping(
            "/egresos/plantilla"
    )
    public ResponseEntity<byte[]> descargarPlantillaEgresos() {

        byte[] archivo =
                plantillaCargaMasivaService.generar();


        return ResponseEntity
                .ok()
                .contentType(
                        TIPO_CONTENIDO_EXCEL
                )
                .contentLength(
                        archivo.length
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + NOMBRE_PLANTILLA
                                + "\""
                )
                .header(
                        HttpHeaders.CACHE_CONTROL,
                        "no-store"
                )
                .body(
                        archivo
                );
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