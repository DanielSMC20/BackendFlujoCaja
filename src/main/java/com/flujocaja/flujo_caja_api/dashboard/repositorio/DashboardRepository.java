package com.flujocaja.flujo_caja_api.dashboard.repositorio;

import com.flujocaja.flujo_caja_api.dashboard.dto.DashboardResponse;
import com.flujocaja.flujo_caja_api.dashboard.dto.DashboardResumenResponse;
import com.flujocaja.flujo_caja_api.dashboard.dto.FlujoDiarioResponse;
import com.flujocaja.flujo_caja_api.dashboard.dto.UltimoMovimientoResponse;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class DashboardRepository {

    private final SimpleJdbcCall paDashboardSel;


    private final RowMapper<DashboardResumenResponse> resumenMapper =
            (rs, rowNum) ->
                    new DashboardResumenResponse(

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
                                    "nSaldoReal"
                            ),

                            rs.getBigDecimal(
                                    "nSaldoProyectado"
                            )
                    );


    private final RowMapper<FlujoDiarioResponse> flujoDiarioMapper =
            (rs, rowNum) ->
                    new FlujoDiarioResponse(

                            rs.getDate(
                                    "dFechaMovimiento"
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
                                    "nSaldoAcumuladoReal"
                            ),

                            rs.getBigDecimal(
                                    "nSaldoAcumuladoProyectado"
                            )
                    );


    private final RowMapper<UltimoMovimientoResponse> ultimoMovimientoMapper =
            (rs, rowNum) -> {

                Boolean cancelado =
                        rs.getObject(
                                "bCancelado"
                        ) == null
                                ? null
                                : rs.getBoolean(
                                "bCancelado"
                        );


                return new UltimoMovimientoResponse(

                        rs.getLong(
                                "nMovimientoId"
                        ),

                        rs.getDate(
                                "dFechaMovimiento"
                        ).toLocalDate(),

                        rs.getInt(
                                "nTipoMovimiento"
                        ),

                        rs.getString(
                                "cTipoMovimiento"
                        ),

                        rs.getInt(
                                "nCategoriaMovimientoId"
                        ),

                        rs.getString(
                                "cCategoria"
                        ),

                        rs.getString(
                                "cDescripcion"
                        ),

                        rs.getBigDecimal(
                                "nMonto"
                        ),

                        cancelado,

                        rs.getString(
                                "cEstado"
                        ),

                        rs.getInt(
                                "nMoneda"
                        ),

                        rs.getString(
                                "cAbreviaturaMoneda"
                        )
                );
            };


    public DashboardRepository(
            DataSource dataSource
    ) {

        this.paDashboardSel =
                new SimpleJdbcCall(dataSource)

                        .withSchemaName(
                                "Finanzas"
                        )

                        .withProcedureName(
                                "PA_Dashboard_Sel"
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
                                ),

                                new SqlParameter(
                                        "nCantidadUltimos",
                                        Types.INTEGER
                                )
                        )

                        /*
                         * IMPORTANTE:
                         *
                         * Se registran en el mismo
                         * orden en que el PA hace
                         * los SELECT.
                         */

                        .returningResultSet(
                                "resumen",
                                resumenMapper
                        )

                        .returningResultSet(
                                "flujoDiario",
                                flujoDiarioMapper
                        )

                        .returningResultSet(
                                "ultimosMovimientos",
                                ultimoMovimientoMapper
                        );
    }


    @SuppressWarnings("unchecked")
    public DashboardResponse obtener(
            Integer empresaId,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Integer cantidadUltimos
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
                        )

                        .addValue(
                                "nCantidadUltimos",
                                cantidadUltimos,
                                Types.INTEGER
                        );


        Map<String, Object> resultado =
                paDashboardSel.execute(
                        parametros
                );


        List<DashboardResumenResponse> resumenLista =
                (List<DashboardResumenResponse>)
                        resultado.getOrDefault(
                                "resumen",
                                List.of()
                        );


        List<FlujoDiarioResponse> flujoDiario =
                (List<FlujoDiarioResponse>)
                        resultado.getOrDefault(
                                "flujoDiario",
                                List.of()
                        );


        List<UltimoMovimientoResponse> ultimosMovimientos =
                (List<UltimoMovimientoResponse>)
                        resultado.getOrDefault(
                                "ultimosMovimientos",
                                List.of()
                        );


        DashboardResumenResponse resumen =
                resumenLista.isEmpty()
                        ? new DashboardResumenResponse(
                        java.math.BigDecimal.ZERO,
                        java.math.BigDecimal.ZERO,
                        java.math.BigDecimal.ZERO,
                        java.math.BigDecimal.ZERO,
                        java.math.BigDecimal.ZERO
                )
                        : resumenLista.get(0);


        return new DashboardResponse(
                resumen,
                flujoDiario,
                ultimosMovimientos
        );
    }
}