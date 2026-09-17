package com.flujocaja.flujo_caja_api.cargamasiva.repositorio;

import com.flujocaja.flujo_caja_api.cargamasiva.dto.CargaMasivaErrorResponse;
import com.flujocaja.flujo_caja_api.cargamasiva.dto.CargaMasivaResponse;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class CargaMasivaRepository {

    private final SimpleJdbcCall paCargaMasiva;


    /*
     * Resultado interno del primer result set.
     */
    private record CargaResumen(

            Long cargaMasivaId,

            Integer estadoCarga,

            String estadoCargaDescripcion,

            Integer totalFilas,

            Integer filasValidas,

            Integer filasError,

            Integer totalProyectados,

            Integer totalCancelados,

            BigDecimal montoProyectado,

            BigDecimal montoCancelado,

            BigDecimal montoTotal

    ) {
    }


    private final RowMapper<CargaResumen> resumenMapper =
            (rs, rowNum) ->
                    new CargaResumen(

                            rs.getLong(
                                    "nCargaMasivaId"
                            ),

                            rs.getInt(
                                    "nEstadoCarga"
                            ),

                            rs.getString(
                                    "cEstadoCarga"
                            ),

                            rs.getInt(
                                    "nTotalFilas"
                            ),

                            rs.getInt(
                                    "nFilasValidas"
                            ),

                            rs.getInt(
                                    "nFilasError"
                            ),

                            rs.getInt(
                                    "nTotalProyectados"
                            ),

                            rs.getInt(
                                    "nTotalCancelados"
                            ),

                            rs.getBigDecimal(
                                    "nMontoProyectado"
                            ),

                            rs.getBigDecimal(
                                    "nMontoCancelado"
                            ),

                            rs.getBigDecimal(
                                    "nMontoTotal"
                            )
                    );


    private final RowMapper<Long> movimientoMapper =
            (rs, rowNum) ->
                    rs.getLong(
                            "nMovimientoId"
                    );


    public CargaMasivaRepository(
            DataSource dataSource
    ) {

        this.paCargaMasiva =
                new SimpleJdbcCall(dataSource)

                        .withProcedureName(
                                "PA_Movimiento_Ins_CargaMasiva"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "cNombreArchivo",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "cHashArchivo",
                                        Types.CHAR
                                ),

                                new SqlParameter(
                                        "nTotalFilas",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "jsonMovimientos",
                                        Types.LONGVARCHAR
                                ),

                                new SqlParameter(
                                        "cObservacion",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "nUsuarioId",
                                        Types.BIGINT
                                )
                        )

                        .returningResultSet(
                                "resumen",
                                resumenMapper
                        )

                        .returningResultSet(
                                "movimientos",
                                movimientoMapper
                        );
    }


    @SuppressWarnings("unchecked")
    public CargaMasivaResponse procesar(
            Integer empresaId,
            Long usuarioId,
            String nombreArchivo,
            String hashCarga,
            Integer totalFilas,
            String json,
            String observacion
    ) {

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()

                        .addValue(
                                "nEmpresaId",
                                empresaId,
                                Types.INTEGER
                        )

                        .addValue(
                                "cNombreArchivo",
                                nombreArchivo,
                                Types.VARCHAR
                        )

                        .addValue(
                                "cHashArchivo",
                                hashCarga,
                                Types.CHAR
                        )

                        .addValue(
                                "nTotalFilas",
                                totalFilas,
                                Types.INTEGER
                        )

                        .addValue(
                                "jsonMovimientos",
                                json,
                                Types.LONGVARCHAR
                        )

                        .addValue(
                                "cObservacion",
                                observacion,
                                Types.VARCHAR
                        )

                        .addValue(
                                "nUsuarioId",
                                usuarioId,
                                Types.BIGINT
                        );


        Map<String, Object> resultado =
                paCargaMasiva.execute(
                        parametros
                );


        List<CargaResumen> resumenLista =
                (List<CargaResumen>)
                        resultado.getOrDefault(
                                "resumen",
                                List.of()
                        );


        List<Long> movimientosCreados =
                (List<Long>)
                        resultado.getOrDefault(
                                "movimientos",
                                List.of()
                        );

        List<CargaMasivaErrorResponse> errores =
                List.of();


        if (resumenLista.isEmpty()) {

            throw new IllegalStateException(
                    "No se obtuvo el resultado de la carga masiva."
            );
        }


        CargaResumen resumen =
                resumenLista.get(0);


        return new CargaMasivaResponse(

                resumen.cargaMasivaId(),

                resumen.estadoCarga(),

                resumen.estadoCargaDescripcion(),

                resumen.totalFilas(),

                resumen.filasValidas(),

                resumen.filasError(),

                resumen.totalProyectados(),

                resumen.totalCancelados(),

                resumen.montoProyectado(),

                resumen.montoCancelado(),

                resumen.montoTotal(),

                errores
        );
    }
}