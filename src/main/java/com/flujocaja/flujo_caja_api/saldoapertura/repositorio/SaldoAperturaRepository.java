package com.flujocaja.flujo_caja_api.saldoapertura.repositorio;

import com.flujocaja.flujo_caja_api.saldoapertura.dto.SaldoAperturaResponse;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SaldoAperturaRepository {

    private final SimpleJdbcCall paSaldoAperturaIns;
    private final SimpleJdbcCall paSaldoAperturaUpd;
    private final SimpleJdbcCall paSaldoAperturaSel;

    private final RowMapper<SaldoAperturaResponse> saldoAperturaMapper =
            (rs, rowNum) -> {

                java.sql.Date fecha =
                        rs.getDate("dFechaApertura");

                java.sql.Timestamp registro =
                        rs.getTimestamp("dFechaRegistro");

                long usuarioId =
                        rs.getLong("nUsuarioRegistroId");

                Long usuarioRegistroId =
                        rs.wasNull() ? null : usuarioId;

                int monedaId = rs.getInt("nMoneda");

                Integer moneda =
                        rs.wasNull() ? null : monedaId;

                return new SaldoAperturaResponse(
                        rs.getBoolean("bConfigurado"),
                        rs.getBigDecimal("nSaldoInicial"),
                        fecha == null
                                ? null
                                : fecha.toLocalDate(),
                        moneda,
                        rs.getString("cMoneda"),
                        usuarioRegistroId,
                        registro == null
                                ? null
                                : registro.toLocalDateTime()
                );
            };

    public SaldoAperturaRepository(DataSource dataSource) {

        // Registrar saldo inicial
        this.paSaldoAperturaIns =
                new SimpleJdbcCall(dataSource)
                        .withProcedureName("PA_SaldoApertura_Ins")
                        .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(
                                new SqlParameter(
                                        "p_nEmpresaId",
                                        Types.INTEGER
                                ),
                                new SqlParameter(
                                        "p_nSaldoInicial",
                                        Types.DECIMAL
                                ),
                                new SqlParameter(
                                        "p_dFechaApertura",
                                        Types.DATE
                                ),
                                new SqlParameter(
                                        "p_nUsuarioId",
                                        Types.BIGINT
                                )
                        )
                        .returningResultSet(
                                "saldoApertura",
                                saldoAperturaMapper
                        );

        // Editar saldo inicial
        this.paSaldoAperturaUpd =
                new SimpleJdbcCall(dataSource)
                        .withProcedureName("PA_SaldoApertura_Upd")
                        .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(
                                new SqlParameter(
                                        "p_nEmpresaId",
                                        Types.INTEGER
                                ),
                                new SqlParameter(
                                        "p_nSaldoInicial",
                                        Types.DECIMAL
                                ),
                                new SqlParameter(
                                        "p_dFechaApertura",
                                        Types.DATE
                                ),
                                new SqlParameter(
                                        "p_nUsuarioId",
                                        Types.BIGINT
                                )
                        );

        // Consultar saldo inicial
        this.paSaldoAperturaSel =
                new SimpleJdbcCall(dataSource)
                        .withProcedureName("PA_SaldoApertura_Sel")
                        .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(
                                new SqlParameter(
                                        "p_nEmpresaId",
                                        Types.INTEGER
                                )
                        )
                        .returningResultSet(
                                "saldoApertura",
                                saldoAperturaMapper
                        );


    }

    public SaldoAperturaResponse registrar(
            Integer empresaId,
            BigDecimal saldoInicial,
            LocalDate fechaApertura,
            Long usuarioId
    ) {

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue("p_nEmpresaId", empresaId, Types.INTEGER)
                        .addValue("p_nSaldoInicial", saldoInicial, Types.DECIMAL)
                        .addValue("p_dFechaApertura", fechaApertura, Types.DATE)
                        .addValue("p_nUsuarioId", usuarioId, Types.BIGINT);

        Map<String, Object> resultado =
                paSaldoAperturaIns.execute(parametros);

        return obtenerLista(resultado)
                .stream()
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(
                                "No se obtuvo el saldo de apertura registrado."
                        )
                );
    }

    public SaldoAperturaResponse editar(
            Integer empresaId,
            BigDecimal saldoInicial,
            LocalDate fechaApertura,
            Long usuarioId
    ) {

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue(
                                "p_nEmpresaId",
                                empresaId,
                                Types.INTEGER
                        )
                        .addValue(
                                "p_nSaldoInicial",
                                saldoInicial,
                                Types.DECIMAL
                        )
                        .addValue(
                                "p_dFechaApertura",
                                fechaApertura,
                                Types.DATE
                        )
                        .addValue(
                                "p_nUsuarioId",
                                usuarioId,
                                Types.BIGINT
                        );

        paSaldoAperturaUpd.execute(parametros);

        return obtener(empresaId)
                .filter(SaldoAperturaResponse::configurado)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "No se obtuvo el saldo de apertura actualizado."
                        )
                );
    }

    public Optional<SaldoAperturaResponse> obtener(
            Integer empresaId
    ) {

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue("p_nEmpresaId", empresaId, Types.INTEGER);

        Map<String, Object> resultado =
                paSaldoAperturaSel.execute(parametros);

        return obtenerLista(resultado)
                .stream()
                .findFirst();
    }

    @SuppressWarnings("unchecked")
    private List<SaldoAperturaResponse> obtenerLista(
            Map<String, Object> resultado
    ) {

        return (List<SaldoAperturaResponse>)
                resultado.getOrDefault(
                        "saldoApertura",
                        List.of()
                );
    }
}