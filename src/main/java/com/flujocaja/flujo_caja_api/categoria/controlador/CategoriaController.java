package com.flujocaja.flujo_caja_api.categoria.controlador;

import com.flujocaja.flujo_caja_api.categoria.dto.CategoriaActualizarRequest;
import com.flujocaja.flujo_caja_api.categoria.dto.CategoriaCrearRequest;
import com.flujocaja.flujo_caja_api.categoria.dto.CategoriaResponse;
import com.flujocaja.flujo_caja_api.categoria.servicio.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;


    public CategoriaController(
            CategoriaService categoriaService
    ) {

        this.categoriaService =
                categoriaService;
    }


    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar(

            @RequestParam(
                    required = false
            )
            Integer tipoMovimiento,

            @RequestParam(
                    defaultValue = "true"
            )
            Boolean soloActivos
    ) {

        return ResponseEntity.ok(

                categoriaService.listar(
                        tipoMovimiento,
                        soloActivos
                )
        );
    }


    @PostMapping
    public ResponseEntity<CategoriaResponse> registrar(

            @Valid
            @RequestBody
            CategoriaCrearRequest request
    ) {

        CategoriaResponse categoria =
                categoriaService.registrar(
                        request
                );


        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        categoria
                );
    }


    @PutMapping("/{categoriaId}")
    public ResponseEntity<CategoriaResponse> actualizar(

            @PathVariable
            Integer categoriaId,

            @Valid
            @RequestBody
            CategoriaActualizarRequest request
    ) {

        return ResponseEntity.ok(

                categoriaService.actualizar(
                        categoriaId,
                        request
                )
        );
    }
}