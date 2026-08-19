package com.flujocaja.flujo_caja_api.seguridad.repositorio;

import com.flujocaja.flujo_caja_api.seguridad.modelo.ContextoAutorizacion;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.Types;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AutorizacionRepository {

    private final SimpleJdbcCall paContextoAutorizacion;


    private record ContextoBase(

            Long usuarioId,

            Integer empresaId,

            String correo,

            Boolean debeCambiarPassword

    ) {
    }


    private record RolBase(
            String codigo
    ) {
    }


    private final RowMapper<ContextoBase> contextoMapper =
            (rs, rowNum) ->
                    new ContextoBase(

                            rs.getLong(
                                    "nUsuarioId"
                            ),

                            rs.getInt(
                                    "nEmpresaId"
                            ),

                            rs.getString(
                                    "cCorreo"
                            ),

                            rs.getBoolean(
                                    "bDebeCambiarPassword"
                            )
                    );


    private final RowMapper<RolBase> rolMapper =
            (rs, rowNum) ->
                    new RolBase(

                            rs.getString(
                                    "cCodigo"
                            )
                    );


    public AutorizacionRepository(
            DataSource dataSource
    ) {

        this.paContextoAutorizacion =
                new SimpleJdbcCall(dataSource)

                        .withProcedureName(
                                "PA_Usuario_Sel_ContextoAutorizacion"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "nUsuarioId",
                                        Types.BIGINT
                                ),

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                )
                        )

                        .returningResultSet(
                                "contexto",
                                contextoMapper
                        )

                        .returningResultSet(
                                "roles",
                                rolMapper
                        );
    }


    @SuppressWarnings("unchecked")
    public Optional<ContextoAutorizacion> obtener(

            Long usuarioId,

            Integer empresaId
    ) {

        Map<String, Object> resultado =
                paContextoAutorizacion.execute(

                        new MapSqlParameterSource()

                                .addValue(
                                        "nUsuarioId",
                                        usuarioId,
                                        Types.BIGINT
                                )

                                .addValue(
                                        "nEmpresaId",
                                        empresaId,
                                        Types.INTEGER
                                )
                );


        List<ContextoBase> contextos =
                (List<ContextoBase>)
                        resultado.getOrDefault(
                                "contexto",
                                List.of()
                        );


        if (contextos.isEmpty()) {

            return Optional.empty();
        }


        ContextoBase contexto =
                contextos.get(0);


        List<RolBase> rolesDb =
                (List<RolBase>)
                        resultado.getOrDefault(
                                "roles",
                                List.of()
                        );


        List<String> roles =
                rolesDb
                        .stream()

                        .map(
                                RolBase::codigo
                        )

                        .distinct()

                        .toList();


        if (roles.isEmpty()) {

            return Optional.empty();
        }


        return Optional.of(

                new ContextoAutorizacion(

                        contexto.usuarioId(),

                        contexto.empresaId(),

                        contexto.correo(),

                        roles,

                        contexto.debeCambiarPassword()
                )
        );
    }
}