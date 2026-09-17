package com.flujocaja.flujo_caja_api.reporte.repositorio;

import com.flujocaja.flujo_caja_api.reporte.dto.ReporteMovimientoDetalleResponse;
import com.flujocaja.flujo_caja_api.reporte.dto.ReporteMovimientoResponse;
import com.flujocaja.flujo_caja_api.reporte.dto.ReporteMovimientoResumenResponse;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class ReporteMovimientoRepository {

    private final SimpleJdbcCall paReporteMovimientoSel;


    private final RowMapper<ReporteMovimientoResumenResponse> resumenMapper =
            (rs, rowNum) ->
                    new ReporteMovimientoResumenResponse(

                            rs.getInt(
                                    "nCantidadRegistros"
                            ),

                            rs.getInt(
                                    "nCantidadAnulados"
                            ),

                            rs.getBigDecimal(
                                    "nTotalIngresos"
                            ),

                            rs.getBigDecimal(
                                    "nTotalEgresosPagados"
                            ),

                            rs.getBigDecimal(
                                    "nTotalEgresosProyectados"
                            )
                    );


    private final RowMapper<ReporteMovimientoDetalleResponse> detalleMapper =
            (rs, rowNum) -> {

                Boolean cancelado =
                        rs.getObject("bCancelado") == null
                                ? null
                                : rs.getBoolean("bCancelado");


                Timestamp fechaRegistro =
                        rs.getTimestamp("dFechaRegistro");

                Timestamp fechaModificacion =
                        rs.getTimestamp("dFechaModificacion");

                Timestamp fechaAnulacion =
                        rs.getTimestamp("dFechaAnulacion");


                return new ReporteMovimientoDetalleResponse(

                        rs.getLong(
                                "nMovimientoId"
                        ),

                        rs.getDate(
                                "dFechaMovimiento"
                        ).toLocalDate(),

                        rs.getDate("dFechaProyectada") == null
                                ? null
                                : rs.getDate(
                                "dFechaProyectada"
                        ).toLocalDate(),

                        rs.getDate("dFechaPago") == null
                                ? null
                                : rs.getDate(
                                "dFechaPago"
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

                        rs.getInt(
                                "nMedioPago"
                        ),

                        rs.getString(
                                "cMedioPago"
                        ),

                        rs.getInt(
                                "nTipoComprobante"
                        ),

                        rs.getString(
                                "cTipoComprobante"
                        ),

                        rs.getInt(
                                "nMoneda"
                        ),

                        rs.getString(
                                "cMoneda"
                        ),

                        rs.getString(
                                "cAbreviaturaMoneda"
                        ),

                        rs.getInt(
                                "nOrigenRegistro"
                        ),

                        rs.getString(
                                "cOrigenRegistro"
                        ),

                        cancelado,

                        rs.getString(
                                "cEstado"
                        ),

                        rs.getString(
                                "cObservacion"
                        ),

                        rs.getBoolean(
                                "bActivo"
                        ),

                        fechaRegistro == null
                                ? null
                                : fechaRegistro.toLocalDateTime(),

                        fechaModificacion == null
                                ? null
                                : fechaModificacion.toLocalDateTime(),

                        fechaAnulacion == null
                                ? null
                                : fechaAnulacion.toLocalDateTime(),

                        rs.getString(
                                "cMotivoAnulacion"
                        )
                );
            };


    public ReporteMovimientoRepository(
            DataSource dataSource
    ) {

        paReporteMovimientoSel =
                new SimpleJdbcCall(dataSource)



                        .withProcedureName(
                                "PA_ReporteMovimiento_Sel"
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
                                        "nTipoMovimiento",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nCategoriaMovimientoId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "bCancelado",
                                        Types.TINYINT
                                ),

                                new SqlParameter(
                                        "nOrigenRegistro",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "bIncluirAnulados",
                                        Types.TINYINT
                                )
                        )

                        .returningResultSet(
                                "resumen",
                                resumenMapper
                        )

                        .returningResultSet(
                                "movimientos",
                                detalleMapper
                        );
    }


    @SuppressWarnings("unchecked")
    public ReporteMovimientoResponse obtener(
            Integer empresaId,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Integer tipoMovimiento,
            Integer categoriaId,
            Boolean cancelado,
            Integer origenRegistro,
            Boolean incluirAnulados
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
                                "nTipoMovimiento",
                                tipoMovimiento,
                                Types.INTEGER
                        )

                        .addValue(
                                "nCategoriaMovimientoId",
                                categoriaId,
                                Types.INTEGER
                        )

                        .addValue(
                                "bCancelado",
                                cancelado == null
                                        ? null
                                        : cancelado ? 1 : 0,
                                Types.TINYINT
                        )

                        .addValue(
                                "nOrigenRegistro",
                                origenRegistro,
                                Types.INTEGER
                        )

                        .addValue(
                                "bIncluirAnulados",
                                Boolean.TRUE.equals(incluirAnulados)
                                        ? 1
                                        : 0,
                                Types.TINYINT
                        );


        Map<String, Object> resultado =
                paReporteMovimientoSel.execute(
                        parametros
                );


        List<ReporteMovimientoResumenResponse> resumenLista =
                (List<ReporteMovimientoResumenResponse>)
                        resultado.getOrDefault(
                                "resumen",
                                List.of()
                        );


        List<ReporteMovimientoDetalleResponse> movimientos =
                (List<ReporteMovimientoDetalleResponse>)
                        resultado.getOrDefault(
                                "movimientos",
                                List.of()
                        );


        ReporteMovimientoResumenResponse resumen =
                resumenLista.isEmpty()
                        ? new ReporteMovimientoResumenResponse(
                        0,
                        0,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO
                )
                        : resumenLista.get(0);


        return new ReporteMovimientoResponse(
                resumen,
                movimientos
        );
    }
}