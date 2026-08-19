package com.flujocaja.flujo_caja_api.constante.repositorio;

import com.flujocaja.flujo_caja_api.constante.dto.ConstanteResponse;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class ConstanteRepository {

    private final SimpleJdbcCall paConstanteSel;

    public ConstanteRepository(DataSource dataSource) {

        this.paConstanteSel = new SimpleJdbcCall(dataSource)
                .withSchemaName("dbo")
                .withProcedureName("PA_Constante_Sel")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter(
                                "nConsCod",
                                Types.SMALLINT
                        )
                )
                .returningResultSet(
                        "constantes",
                        (rs, rowNum) ->
                                new ConstanteResponse(
                                        rs.getInt("nConsValor"),
                                        rs.getString("cConsDescripcion"),
                                        rs.getString("cConsAbreviatura")
                                )
                );
    }

    @SuppressWarnings("unchecked")
    public List<ConstanteResponse> listarPorCodigo(
            short nConsCod
    ) {

        MapSqlParameterSource parametros = new MapSqlParameterSource().addValue("nConsCod",nConsCod);
        Map<String, Object> resultado = paConstanteSel.execute(parametros);
        return (List<ConstanteResponse>) resultado.getOrDefault("constantes", List.of());
    }
}