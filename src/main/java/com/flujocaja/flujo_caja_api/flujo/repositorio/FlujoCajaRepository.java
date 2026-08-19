package com.flujocaja.flujo_caja_api.flujo.repositorio;

import com.flujocaja.flujo_caja_api.flujo.dto.FlujoCajaDiarioResponse;
import com.flujocaja.flujo_caja_api.flujo.dto.FlujoCajaResponse;
import com.flujocaja.flujo_caja_api.flujo.dto.FlujoCajaResumenResponse;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class FlujoCajaRepository {

    private final SimpleJdbcCall paFlujoCajaSel;


    private final RowMapper<FlujoCajaResumenResponse> resumenMapper =
            (rs, rowNum) ->

                    new FlujoCajaResumenResponse(

                            rs.getDate(
                                    "dFechaDesde"
                            ).toLocalDate(),

                            rs.getDate(
                                    "dFechaHasta"
                            ).toLocalDate(),

                            rs.getBigDecimal(
                                    "nSaldoInicialReal"
                            ),

                            rs.getBigDecimal(
                                    "nSaldoInicialProyectado"
                            ),

                            rs.getBigDecimal(
                                    "nTotalIngresos"
                            ),

                            rs.getBigDecimal(
                                    "nTotalEgresosPagados"
                            ),

                            rs.getBigDecimal(
                                    "nTotalEgresosProyectados"
                            ),

                            rs.getBigDecimal(
                                    "nSaldoFinalReal"
                            ),

                            rs.getBigDecimal(
                                    "nSaldoFinalProyectado"
                            )
                    );


    private final RowMapper<FlujoCajaDiarioResponse> detalleMapper =
            (rs, rowNum) ->

                    new FlujoCajaDiarioResponse(

                            rs.getDate(
                                    "dFecha"
                            ).toLocalDate(),

                            rs.getBigDecimal(
                                    "nIngresos"
                            ),

                            rs.getBigDecimal(
                                    "nEgresosPagados"
                            ),

                            rs.getBigDecimal(
                                    "nEgresosProyectados"
                            ),

                            rs.getBigDecimal(
                                    "nFlujoReal"
                            ),

                            rs.getBigDecimal(
                                    "nFlujoProyectado"
                            ),

                            rs.getBigDecimal(
                                    "nSaldoReal"
                            ),

                            rs.getBigDecimal(
                                    "nSaldoProyectado"
                            )
                    );


    public FlujoCajaRepository(
            DataSource dataSource
    ) {

        this.paFlujoCajaSel =
                new SimpleJdbcCall(dataSource)

                        .withSchemaName(
                                "Finanzas"
                        )

                        .withProcedureName(
                                "PA_FlujoCaja_Sel"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "dFechaDesde",
                                        Types.DATE
                                ),

                                new SqlParameter(
                                        "dFechaHasta",
                                        Types.DATE
                                )
                        )

                        /*
                         * El orden tiene que ser igual
                         * al orden de SELECT del PA.
                         */

                        .returningResultSet(
                                "resumen",
                                resumenMapper
                        )

                        .returningResultSet(
                                "detalle",
                                detalleMapper
                        );
    }


    @SuppressWarnings("unchecked")
    public FlujoCajaResponse obtener(
            Integer empresaId,
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()

                        .addValue(
                                "nEmpresaId",
                                empresaId,
                                Types.INTEGER
                        )

                        .addValue(
                                "dFechaDesde",
                                fechaDesde,
                                Types.DATE
                        )

                        .addValue(
                                "dFechaHasta",
                                fechaHasta,
                                Types.DATE
                        );


        Map<String, Object> resultado =
                paFlujoCajaSel.execute(
                        parametros
                );


        List<FlujoCajaResumenResponse> resumenLista =
                (List<FlujoCajaResumenResponse>)
                        resultado.getOrDefault(
                                "resumen",
                                List.of()
                        );


        List<FlujoCajaDiarioResponse> detalle =
                (List<FlujoCajaDiarioResponse>)
                        resultado.getOrDefault(
                                "detalle",
                                List.of()
                        );


        FlujoCajaResumenResponse resumen;


        if (resumenLista.isEmpty()) {

            LocalDate desde =
                    fechaDesde != null
                            ? fechaDesde
                            : LocalDate.now()
                            .withDayOfMonth(1);


            LocalDate hasta =
                    fechaHasta != null
                            ? fechaHasta
                            : LocalDate.now();


            resumen =
                    new FlujoCajaResumenResponse(

                            desde,
                            hasta,

                            BigDecimal.ZERO,
                            BigDecimal.ZERO,

                            BigDecimal.ZERO,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO,

                            BigDecimal.ZERO,
                            BigDecimal.ZERO
                    );

        } else {

            resumen =
                    resumenLista.get(0);
        }


        return new FlujoCajaResponse(
                resumen,
                detalle
        );
    }
}