package com.flujocaja.flujo_caja_api.constante.controlador;

import com.flujocaja.flujo_caja_api.constante.dto.ConstanteResponse;
import com.flujocaja.flujo_caja_api.constante.servicio.ConstanteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/constantes")
public class ConstanteController {

    private final ConstanteService constanteService;

    public ConstanteController(
            ConstanteService constanteService
    ) {
        this.constanteService =
                constanteService;
    }

    @GetMapping("/{nConsCod}")
    public ResponseEntity<List<ConstanteResponse>> listar(
            @PathVariable short nConsCod
    ) {

        return ResponseEntity.ok(
                constanteService.listar(nConsCod)
        );
    }
}