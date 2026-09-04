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
    private final SimpleJdbcCall paSaldoAperturaSel;

    private final RowMapper<SaldoAperturaResponse> saldoAperturaMapper =
            (rs, rowNum) ->
                    new SaldoAperturaResponse(
                            rs.getBoolean("bConfigurado"),
                            rs.getBigDecimal("nSaldoInicial"),
                            rs.getDate("dFechaApertura").toLocalDate(),
                            rs.getInt("nMoneda"),
                            rs.getString("cMoneda"),
                            rs.getLong("nUsuarioRegistroId"),
                            rs.getTimestamp("dFechaRegistro")
                                    .toLocalDateTime()
                    );

    public SaldoAperturaRepository(
            DataSource dataSource
    ) {

        this.paSaldoAperturaIns =
                new SimpleJdbcCall(dataSource)
                        .withProcedureName("PA_SaldoApertura_Ins")
                        .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(
                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),
                                new SqlParameter(
                                        "nSaldoInicial",
                                        Types.DECIMAL
                                ),
                                new SqlParameter(
                                        "dFechaApertura",
                                        Types.DATE
                                ),
                                new SqlParameter(
                                        "nUsuarioId",
                                        Types.BIGINT
                                )
                        )
                        .returningResultSet(
                                "saldoApertura",
                                saldoAperturaMapper
                        );

        this.paSaldoAperturaSel =
                new SimpleJdbcCall(dataSource)
                        .withProcedureName("PA_SaldoApertura_Sel")
                        .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(
                                new SqlParameter(
                                        "nEmpresaId",
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
                        .addValue(
                                "nEmpresaId",
                                empresaId,
                                Types.INTEGER
                        )
                        .addValue(
                                "nSaldoInicial",
                                saldoInicial,
                                Types.DECIMAL
                        )
                        .addValue(
                                "dFechaApertura",
                                fechaApertura,
                                Types.DATE
                        )
                        .addValue(
                                "nUsuarioId",
                                usuarioId,
                                Types.BIGINT
                        );

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

    public Optional<SaldoAperturaResponse> obtener(
            Integer empresaId
    ) {

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue(
                                "nEmpresaId",
                                empresaId,
                                Types.INTEGER
                        );

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
