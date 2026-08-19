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


    private final RowMapper<CargaMasivaErrorResponse> errorMapper =
            (rs, rowNum) ->
                    new CargaMasivaErrorResponse(

                            rs.getObject(
                                    "nNumeroRegistro",
                                    Integer.class
                            ),

                            rs.getObject(
                                    "nFilaExcel",
                                    Integer.class
                            ),

                            rs.getString(
                                    "cError"
                            )
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
                                        Types.NVARCHAR
                                ),

                                new SqlParameter(
                                        "cHashArchivo",
                                        Types.CHAR
                                ),

                                new SqlParameter(
                                        "nUsuarioRegistroId",
                                        Types.BIGINT
                                ),

                                new SqlParameter(
                                        "cJson",
                                        Types.LONGNVARCHAR
                                )
                        )

                        .returningResultSet(
                                "resumen",
                                resumenMapper
                        )

                        .returningResultSet(
                                "errores",
                                errorMapper
                        );
    }


    @SuppressWarnings("unchecked")
    public CargaMasivaResponse procesar(
            Integer empresaId,
            Long usuarioId,
            String nombreArchivo,
            String hashCarga,
            String json
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
                                Types.NVARCHAR
                        )

                        .addValue(
                                "cHashArchivo",
                                hashCarga,
                                Types.CHAR
                        )

                        .addValue(
                                "nUsuarioRegistroId",
                                usuarioId,
                                Types.BIGINT
                        )

                        .addValue(
                                "cJson",
                                json,
                                Types.LONGNVARCHAR
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


        List<CargaMasivaErrorResponse> errores =
                (List<CargaMasivaErrorResponse>)
                        resultado.getOrDefault(
                                "errores",
                                List.of()
                        );


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