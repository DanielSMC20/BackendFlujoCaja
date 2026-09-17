package com.flujocaja.flujo_caja_api.cargamasiva.servicio;

import com.flujocaja.flujo_caja_api.cargamasiva.dto.CargaMasivaRequest;
import com.flujocaja.flujo_caja_api.cargamasiva.dto.CargaMasivaResponse;
import com.flujocaja.flujo_caja_api.cargamasiva.repositorio.CargaMasivaRepository;
import com.flujocaja.flujo_caja_api.seguridad.servicio.ContextoSeguridad;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.List;

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

        List<MovimientoCargaProcedimiento> movimientos =
                request.filas()
                        .stream()
                        .map(fila ->
                                new MovimientoCargaProcedimiento(

                                        fila.numeroRegistro(),
                                        fila.filaExcel(),

                                        fila.fechaMovimiento(),
                                        fila.categoriaId(),

                                        fila.descripcion().trim(),
                                        fila.monto(),

                                        9, // No especificado
                                        1, // Soles
                                        5, // Sin comprobante

                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,

                                        fila.bCancelado()
                                )
                        )
                        .toList();

        String json =
                jsonMapper.writeValueAsString(
                        movimientos
                );


        /*
         * En el contrato actual, todas las filas
         * recibidas son filas válidas.
         */
        int totalFilas =
                request.filas().size();


        /*
         * SHA-256 del contenido normalizado.
         */
        String hashCarga =
                calcularHash(
                        json
                );


        String observacion =
                "Carga masiva de egresos desde Excel.";


        return cargaMasivaRepository.procesar(

                contextoSeguridad.empresaId(),

                contextoSeguridad.usuarioId(),

                request.nombreArchivo().trim(),

                hashCarga,

                totalFilas,

                json,

                observacion
        );
    }

    private record MovimientoCargaProcedimiento(

            Integer numeroRegistro,
            Integer filaExcel,

            LocalDate fechaMovimiento,
            Integer categoriaId,

            String descripcion,
            BigDecimal monto,

            Integer medioPago,
            Integer moneda,
            Integer tipoComprobante,

            LocalDate fechaComprobante,
            String serieComprobante,
            String numeroComprobante,
            String documentoEmisor,
            String razonSocialEmisor,

            String observacion,

            Integer bCancelado

    ) {
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