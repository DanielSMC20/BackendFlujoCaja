package com.flujocaja.flujo_caja_api.movimiento.repositorio;

import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoDetalleResponse;
import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoResponse;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.Instant;
import java.time.ZoneOffset;
@Repository
public class MovimientoRepository {

    /* =========================================================
       STORED PROCEDURES
       ========================================================= */

    private final SimpleJdbcCall paMovimientoIns;

    private final SimpleJdbcCall paMovimientoSel;

    private final SimpleJdbcCall paMovimientoSelId;

    private final SimpleJdbcCall paMovimientoUpd;

    private final SimpleJdbcCall paMovimientoUpdCancelar;

    private final SimpleJdbcCall paMovimientoUpdAnular;


    /* =========================================================
       MAPPERS
       ========================================================= */

    private final RowMapper<MovimientoResponse> movimientoMapper =
            (rs, rowNum) ->
                    mapearMovimiento(
                            rs
                    );


    private final RowMapper<MovimientoDetalleResponse> movimientoDetalleMapper =
            (rs, rowNum) ->
                    mapearMovimientoDetalle(
                            rs
                    );


    /* =========================================================
       CONSTRUCTOR
       ========================================================= */

    public MovimientoRepository(
            DataSource dataSource
    ) {

        /* =====================================================
           INSERTAR
           ===================================================== */

        this.paMovimientoIns =
                new SimpleJdbcCall(dataSource)
                        .withProcedureName("PA_Movimiento_Ins")
                        .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
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
                                        "dFechaMovimiento",
                                        Types.DATE
                                ),

                                new SqlParameter(
                                        "bCancelado",
                                        Types.BIT
                                ),

                                new SqlParameter(
                                        "cDescripcion",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "nMonto",
                                        Types.DECIMAL
                                ),

                                new SqlParameter(
                                        "nMedioPago",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nTipoComprobante",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nMoneda",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nOrigenRegistro",
                                        Types.INTEGER
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
                                "movimiento",
                                movimientoMapper
                        );

        /* =====================================================
           LISTAR
           ===================================================== */

        this.paMovimientoSel =
                new SimpleJdbcCall(
                        dataSource
                )

                        .withProcedureName(
                                "PA_Movimiento_Sel"
                        )
                        .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nTipoMovimiento",
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
                                        "bCancelado",
                                        Types.BIT
                                ),

                                new SqlParameter(
                                        "bSoloActivos",
                                        Types.BIT
                                )
                        )
                        .returningResultSet(
                                "movimientos",
                                movimientoMapper
                        );


        /* =====================================================
           OBTENER POR ID
           ===================================================== */

        this.paMovimientoSelId =
                new SimpleJdbcCall(
                        dataSource
                )

                        .withProcedureName(
                                "PA_Movimiento_Sel_Id"
                        )
                        .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nMovimientoId",
                                        Types.BIGINT
                                )
                        )
                        .returningResultSet(
                                "movimiento",
                                movimientoDetalleMapper
                        );


        /* =====================================================
           ACTUALIZAR
           ===================================================== */

        this.paMovimientoUpd =
                new SimpleJdbcCall(dataSource)
                        .withProcedureName("PA_Movimiento_Upd")
                        .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(

                                new SqlParameter(
                                        "nMovimientoId",
                                        Types.BIGINT
                                ),

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
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
                                        "dFechaMovimiento",
                                        Types.DATE
                                ),

                                new SqlParameter(
                                        "bCancelado",
                                        Types.BIT
                                ),

                                new SqlParameter(
                                        "cDescripcion",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "nMonto",
                                        Types.DECIMAL
                                ),

                                new SqlParameter(
                                        "nMedioPago",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nTipoComprobante",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nMoneda",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nOrigenRegistro",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "cObservacion",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "nUsuarioId",
                                        Types.BIGINT
                                )
                        );


        /* =====================================================
           PAGAR EGRESO
           ===================================================== */

        this.paMovimientoUpdCancelar =
                new SimpleJdbcCall(dataSource)
                        .withProcedureName("PA_Movimiento_Upd_Cancelar")
                        .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(
                                new SqlParameter(
                                        "nMovimientoId",
                                        Types.BIGINT
                                ),
                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),
                                new SqlParameter(
                                        "dFechaPago",
                                        Types.DATE
                                ),
                                new SqlParameter(
                                        "nUsuarioId",
                                        Types.BIGINT
                                )
                        );




        this.paMovimientoUpdAnular =
                new SimpleJdbcCall(
                        dataSource
                )
                        .withProcedureName(
                                "PA_Movimiento_Upd_Anular"
                        )
                        .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(

                                new SqlParameter(
                                        "p_nMovimientoId",
                                        Types.BIGINT
                                ),

                                new SqlParameter(
                                        "p_nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "p_cMotivoAnulacion",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "p_nUsuarioId",
                                        Types.BIGINT
                                )
                        );
    }


    /* =========================================================
       LISTAR
       ========================================================= */

    @SuppressWarnings("unchecked")
    public List<MovimientoResponse> listar(

            Integer empresaId,

            Integer tipoMovimiento,

            LocalDate fechaDesde,

            LocalDate fechaHasta,

            Boolean cancelado,

            Boolean soloActivos
    ) {

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()

                        .addValue(
                                "nEmpresaId",
                                empresaId,
                                Types.INTEGER
                        )

                        .addValue(
                                "nTipoMovimiento",
                                tipoMovimiento,
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
                                "bCancelado",
                                cancelado,
                                Types.BIT
                        )

                        .addValue(
                                "bSoloActivos",
                                soloActivos,
                                Types.BIT
                        );


        Map<String, Object> resultado =
                paMovimientoSel.execute(
                        parametros
                );


        return (List<MovimientoResponse>)
                resultado.getOrDefault(
                        "movimientos",
                        List.of()
                );
    }


    /* =========================================================
       OBTENER DETALLE
       ========================================================= */

    @SuppressWarnings("unchecked")
    public Optional<MovimientoDetalleResponse> obtenerDetallePorId(

            Integer empresaId,

            Long movimientoId
    ) {

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()

                        .addValue(
                                "nEmpresaId",
                                empresaId,
                                Types.INTEGER
                        )

                        .addValue(
                                "nMovimientoId",
                                movimientoId,
                                Types.BIGINT
                        );


        Map<String, Object> resultado =
                paMovimientoSelId.execute(
                        parametros
                );


        List<MovimientoDetalleResponse> movimientos =
                (List<MovimientoDetalleResponse>)
                        resultado.getOrDefault(
                                "movimiento",
                                List.of()
                        );


        return movimientos
                .stream()
                .findFirst();
    }


    /* =========================================================
       REGISTRAR
       ========================================================= */

    public MovimientoResponse registrar(
            Integer empresaId,
            Integer tipoMovimiento,
            Integer categoriaId,
            LocalDate fechaMovimiento,
            Boolean cancelado,
            String descripcion,
            BigDecimal monto,
            Integer medioPago,
            Integer tipoComprobante,
            Integer moneda,
            Integer origenRegistro,
            String observacion,
            Long usuarioId
    ) {

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()

                        .addValue(
                                "nEmpresaId",
                                empresaId,
                                Types.INTEGER
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
                                "dFechaMovimiento",
                                fechaMovimiento,
                                Types.DATE
                        )

                        .addValue(
                                "bCancelado",
                                cancelado,
                                Types.BIT
                        )

                        .addValue(
                                "cDescripcion",
                                descripcion,
                                Types.VARCHAR
                        )

                        .addValue(
                                "nMonto",
                                monto,
                                Types.DECIMAL
                        )

                        .addValue(
                                "nMedioPago",
                                medioPago,
                                Types.INTEGER
                        )

                        .addValue(
                                "nTipoComprobante",
                                tipoComprobante,
                                Types.INTEGER
                        )

                        .addValue(
                                "nMoneda",
                                moneda,
                                Types.INTEGER
                        )

                        .addValue(
                                "nOrigenRegistro",
                                origenRegistro,
                                Types.INTEGER
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
                paMovimientoIns.execute(
                        parametros
                );


        return obtenerMovimiento(
                resultado
        );
    }


    /* =========================================================
       ACTUALIZAR
       ========================================================= */

    public MovimientoResponse actualizar(

            Long movimientoId,

            Integer empresaId,

            Integer tipoMovimiento,

            Integer categoriaId,

            LocalDate fechaMovimiento,

            Boolean cancelado,

            String descripcion,

            BigDecimal monto,

            Integer medioPago,

            Integer tipoComprobante,

            Integer moneda,

            Integer origenRegistro,

            String observacion,

            Long usuarioId
    ) {

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()

                        .addValue(
                                "nMovimientoId",
                                movimientoId,
                                Types.BIGINT
                        )

                        .addValue(
                                "nEmpresaId",
                                empresaId,
                                Types.INTEGER
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
                                "dFechaMovimiento",
                                fechaMovimiento,
                                Types.DATE
                        )

                        .addValue(
                                "bCancelado",
                                cancelado,
                                Types.BIT
                        )

                        .addValue(
                                "cDescripcion",
                                descripcion,
                                Types.VARCHAR
                        )

                        .addValue(
                                "nMonto",
                                monto,
                                Types.DECIMAL
                        )

                        .addValue(
                                "nMedioPago",
                                medioPago,
                                Types.INTEGER
                        )

                        .addValue(
                                "nTipoComprobante",
                                tipoComprobante,
                                Types.INTEGER
                        )

                        .addValue(
                                "nMoneda",
                                moneda,
                                Types.INTEGER
                        )

                        .addValue(
                                "nOrigenRegistro",
                                origenRegistro,
                                Types.INTEGER
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

        paMovimientoUpd.execute(parametros);

        MovimientoDetalleResponse detalle =
                obtenerDetallePorId(
                        empresaId,
                        movimientoId
                )
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "El movimiento fue actualizado, pero no pudo recuperarse."
                                )
                        );

        return convertirAResponse(detalle);
    }

    /* =========================================================
       MARCAR COMO PAGADO
       ========================================================= */

    public void marcarComoPagado(
            Integer empresaId,
            Long movimientoId,
            LocalDate fechaPago,
            Long usuarioId
    ) {
        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue(
                                "nMovimientoId",
                                movimientoId,
                                Types.BIGINT
                        )
                        .addValue(
                                "nEmpresaId",
                                empresaId,
                                Types.INTEGER
                        )
                        .addValue(
                                "dFechaPago",
                                fechaPago,
                                Types.DATE
                        )
                        .addValue(
                                "nUsuarioId",
                                usuarioId,
                                Types.BIGINT
                        );

        paMovimientoUpdCancelar.execute(parametros);
    }


    /* =========================================================
       ANULAR
       ========================================================= */

    public void anular(

            Integer empresaId,

            Long movimientoId,

            String motivo,

            Long usuarioId
    ) {

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()

                        .addValue(
                                "p_nMovimientoId",
                                movimientoId,
                                Types.BIGINT
                        )

                        .addValue(
                                "p_nEmpresaId",
                                empresaId,
                                Types.INTEGER
                        )

                        .addValue(
                                "p_cMotivoAnulacion",
                                motivo,
                                Types.VARCHAR
                        )

                        .addValue(
                                "p_nUsuarioId",
                                usuarioId,
                                Types.BIGINT
                        );


        paMovimientoUpdAnular.execute(
                parametros
        );
    }


    /* =========================================================
       OBTENER ÚNICO RESULTADO
       ========================================================= */

    @SuppressWarnings("unchecked")
    private MovimientoResponse obtenerMovimiento(
            Map<String, Object> resultado
    ) {

        List<MovimientoResponse> movimientos =
                (List<MovimientoResponse>)
                        resultado.getOrDefault(
                                "movimiento",
                                List.of()
                        );


        if (
                movimientos.isEmpty()
        ) {

            throw new IllegalStateException(
                    "El procedimiento no devolvió el movimiento procesado."
            );
        }


        return movimientos.get(0);
    }


    /* =========================================================
       MAPPER MOVIMIENTO
       ========================================================= */

    private MovimientoResponse mapearMovimiento(
            ResultSet rs
    ) throws SQLException {

        return new MovimientoResponse(

                rs.getLong(
                        "nMovimientoId"
                ),

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

                obtenerFecha(
                        rs,
                        "dFechaMovimiento"
                ),

                obtenerFecha(
                        rs,
                        "dFechaProyectada"
                ),

                obtenerFecha(
                        rs,
                        "dFechaPago"
                ),

                obtenerBooleanNullable(
                        rs,
                        "bCancelado"
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

                rs.getString(
                        "cObservacion"
                ),

                rs.getBoolean(
                        "bActivo"
                )
        );
    }


    /* =========================================================
       MAPPER DETALLE
       ========================================================= */

    private MovimientoDetalleResponse mapearMovimientoDetalle(
            ResultSet rs
    ) throws SQLException {

        return new MovimientoDetalleResponse(

                rs.getLong(
                        "nMovimientoId"
                ),

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

                obtenerFecha(
                        rs,
                        "dFechaMovimiento"
                ),

                obtenerFecha(
                        rs,
                        "dFechaProyectada"
                ),

                obtenerFecha(
                        rs,
                        "dFechaPago"
                ),

                obtenerBooleanNullable(
                        rs,
                        "bCancelado"
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

                rs.getString(
                        "cObservacion"
                ),

                rs.getBoolean(
                        "bActivo"
                ),


                /* =============================================
                   COMPROBANTE
                   ============================================= */

                obtenerFecha(
                        rs,
                        "dFechaComprobante"
                ),

                rs.getString(
                        "cSerieComprobante"
                ),

                rs.getString(
                        "cNumeroComprobante"
                ),

                rs.getString(
                        "cDocumentoEmisor"
                ),

                rs.getString(
                        "cRazonSocialEmisor"
                ),

                rs.getString(
                        "cArchivoXmlNombre"
                ),

                rs.getString(
                        "cHashXml"
                ),
                obtenerLongNullable(
                        rs,
                        "nUsuarioRegistroId"
                ),

                rs.getString(
                        "cUsuarioRegistro"
                ),

                obtenerFechaHora(
                        rs,
                        "dFechaRegistro"
                )
        );
    }


    /* =========================================================
       HELPERS
       ========================================================= */

    private LocalDate obtenerFecha(
            ResultSet rs,
            String columna
    ) throws SQLException {

        java.sql.Date fecha =
                rs.getDate(
                        columna
                );


        return fecha == null
                ? null
                : fecha.toLocalDate();
    }


    private Boolean obtenerBooleanNullable(
            ResultSet rs,
            String columna
    ) throws SQLException {

        Object valor =
                rs.getObject(
                        columna
                );


        if (
                valor == null
        ) {

            return null;
        }


        return rs.getBoolean(
                columna
        );
    }

    private Instant obtenerFechaHora(
            ResultSet rs,
            String columna
    ) throws SQLException {

        java.sql.Timestamp fechaHora =
                rs.getTimestamp(columna);

        return fechaHora == null
                ? null
                : fechaHora
                .toLocalDateTime()
                .toInstant(ZoneOffset.UTC);
    }


    private Long obtenerLongNullable(
            ResultSet rs,
            String columna
    ) throws SQLException {

        Object valor =
                rs.getObject(
                        columna
                );

        if (valor == null) {
            return null;
        }

        return ((Number) valor).longValue();
    }

    private MovimientoResponse convertirAResponse(
            MovimientoDetalleResponse detalle
    ) {

        return new MovimientoResponse(

                detalle.id(),

                detalle.tipoMovimiento(),
                detalle.tipoMovimientoDescripcion(),

                detalle.categoriaId(),
                detalle.categoria(),

                detalle.fechaMovimiento(),
                detalle.fechaProyectada(),
                detalle.fechaPago(),

                detalle.cancelado(),

                detalle.descripcion(),
                detalle.monto(),

                detalle.medioPago(),
                detalle.medioPagoDescripcion(),

                detalle.tipoComprobante(),
                detalle.tipoComprobanteDescripcion(),

                detalle.moneda(),
                detalle.monedaDescripcion(),
                detalle.monedaAbreviatura(),

                detalle.origenRegistro(),
                detalle.origenRegistroDescripcion(),

                detalle.observacion(),

                detalle.activo()
        );
    }
}