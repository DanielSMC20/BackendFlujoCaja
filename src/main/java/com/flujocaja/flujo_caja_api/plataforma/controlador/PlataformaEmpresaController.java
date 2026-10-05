package com.flujocaja.flujo_caja_api.plataforma.controlador;

import com.flujocaja.flujo_caja_api.plataforma.dto.EmpresaPlataformaActualizarRequest;
import com.flujocaja.flujo_caja_api.plataforma.dto.EmpresaPlataformaCrearRequest;
import com.flujocaja.flujo_caja_api.plataforma.dto.EmpresaPlataformaResponse;

import com.flujocaja.flujo_caja_api.plataforma.servicio.PlataformaEmpresaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/plataforma/empresas"
)
public class PlataformaEmpresaController {

    private final PlataformaEmpresaService empresaService;


    public PlataformaEmpresaController(
            PlataformaEmpresaService empresaService
    ) {

        this.empresaService =
                empresaService;
    }


    @GetMapping
    public ResponseEntity<List<EmpresaPlataformaResponse>> listar(

            @RequestParam(
                    required = false
            )
            String busqueda
    ) {

        return ResponseEntity.ok(

                empresaService.listar(
                        busqueda
                )
        );
    }


    @GetMapping(
            "/{empresaId}"
    )
    public ResponseEntity<EmpresaPlataformaResponse> obtener(

            @PathVariable
            Integer empresaId
    ) {

        return ResponseEntity.ok(

                empresaService.obtener(
                        empresaId
                )
        );
    }


    @PostMapping
    public ResponseEntity<EmpresaPlataformaResponse> crear(

            @Valid
            @RequestBody
            EmpresaPlataformaCrearRequest request
    ) {

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )

                .body(

                        empresaService.crear(
                                request
                        )
                );
    }


    @PutMapping(
            "/{empresaId}"
    )
    public ResponseEntity<EmpresaPlataformaResponse> actualizar(

            @PathVariable
            Integer empresaId,

            @Valid
            @RequestBody
            EmpresaPlataformaActualizarRequest request
    ) {

        return ResponseEntity.ok(

                empresaService.actualizar(

                        empresaId,

                        request
                )
        );
    }
}