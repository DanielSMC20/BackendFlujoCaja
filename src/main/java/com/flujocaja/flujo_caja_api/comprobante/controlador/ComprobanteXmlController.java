package com.flujocaja.flujo_caja_api.comprobante.controlador;

import com.flujocaja.flujo_caja_api.comprobante.dto.ComprobanteXmlResponse;
import com.flujocaja.flujo_caja_api.comprobante.servicio.ComprobanteXmlService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(
        "/api/comprobantes/xml"
)
public class ComprobanteXmlController {

    private final ComprobanteXmlService comprobanteXmlService;


    public ComprobanteXmlController(
            ComprobanteXmlService comprobanteXmlService
    ) {

        this.comprobanteXmlService =
                comprobanteXmlService;
    }


    @PostMapping(
            value = "/analizar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ComprobanteXmlResponse> analizar(

            @RequestParam("archivo")
            MultipartFile archivo
    ) {

        return ResponseEntity.ok(

                comprobanteXmlService.analizar(
                        archivo
                )
        );
    }
}