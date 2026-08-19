package com.flujocaja.flujo_caja_api.cargamasiva.servicio;

import com.flujocaja.flujo_caja_api.cargamasiva.dto.CargaMasivaRequest;
import com.flujocaja.flujo_caja_api.cargamasiva.dto.CargaMasivaResponse;
import com.flujocaja.flujo_caja_api.cargamasiva.repositorio.CargaMasivaRepository;
import com.flujocaja.flujo_caja_api.seguridad.servicio.ContextoSeguridad;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class CargaMasivaService {

    private final CargaMasivaRepository cargaMasivaRepository;

    private final ContextoSeguridad contextoSeguridad;

    private final JsonMapper jsonMapper;


    public CargaMasivaService(
            CargaMasivaRepository cargaMasivaRepository,
            ContextoSeguridad contextoSeguridad,
            JsonMapper jsonMapper
    ) {

        this.cargaMasivaRepository =
                cargaMasivaRepository;

        this.contextoSeguridad =
                contextoSeguridad;

        this.jsonMapper =
                jsonMapper;
    }


    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','CONTADOR','OPERADOR')"
    )
    public CargaMasivaResponse procesar(
            CargaMasivaRequest request
    ) {

        /*
         * Solo serializamos las filas.
         *
         * empresaId y usuarioId jamás vienen
         * desde Angular.
         */

        String json =
                jsonMapper.writeValueAsString(
                        request.filas()
                );


        /*
         * SHA-256 calculado en backend.
         *
         * Angular no decide el hash.
         */

        String hashCarga =
                calcularHash(
                        json
                );


        return cargaMasivaRepository.procesar(

                contextoSeguridad.empresaId(),

                contextoSeguridad.usuarioId(),

                request.nombreArchivo().trim(),

                hashCarga,

                json
        );
    }


    private String calcularHash(
            String contenido
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );


            byte[] hash =
                    digest.digest(

                            contenido.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );


            return HexFormat
                    .of()
                    .formatHex(
                            hash
                    );

        } catch (
                NoSuchAlgorithmException ex
        ) {

            throw new IllegalStateException(
                    "No fue posible calcular el hash de la carga.",
                    ex
            );
        }
    }
}