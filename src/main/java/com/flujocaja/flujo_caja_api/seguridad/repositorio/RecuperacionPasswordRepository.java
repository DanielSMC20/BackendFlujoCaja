package com.flujocaja.flujo_caja_api.seguridad.repositorio;

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
public class RecuperacionPasswordRepository {

    private final SimpleJdbcCall paCrearToken;

    private final SimpleJdbcCall paConsumirToken;


    public record UsuarioRecuperacion(
            Long usuarioId,
            String correo,
            String nombreCompleto
    ) {
    }


    private final RowMapper<UsuarioRecuperacion> usuarioMapper =
            (rs, rowNum) ->
                    new UsuarioRecuperacion(
                            rs.getLong("nUsuarioId"),
                            rs.getString("cCorreo"),
                            rs.getString("cNombreCompleto")
                    );


    public RecuperacionPasswordRepository(
            DataSource dataSource
    ) {

        this.paCrearToken =
                new SimpleJdbcCall(dataSource)

                        .withProcedureName(
                                "PA_RecuperacionPassword_Ins_Token"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(
                                new SqlParameter(
                                        "p_cCorreo",
                                        Types.VARCHAR
                                ),
                                new SqlParameter(
                                        "p_cTokenHash",
                                        Types.CHAR
                                ),
                                new SqlParameter(
                                        "p_nExpiracionMinutos",
                                        Types.INTEGER
                                )
                        )

                        .returningResultSet(
                                "usuario",
                                usuarioMapper
                        );


        this.paConsumirToken =
                new SimpleJdbcCall(dataSource)

                        .withProcedureName(
                                "PA_RecuperacionPassword_Upd_ConsumirToken"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(
                                new SqlParameter(
                                        "p_cTokenHash",
                                        Types.CHAR
                                ),
                                new SqlParameter(
                                        "p_cPasswordHash",
                                        Types.VARCHAR
                                )
                        );
    }


    @SuppressWarnings("unchecked")
    public Optional<UsuarioRecuperacion> crearToken(
            String correo,
            String tokenHash,
            int expiracionMinutos
    ) {

        Map<String, Object> resultado =
                paCrearToken.execute(
                        new MapSqlParameterSource()
                                .addValue(
                                        "p_cCorreo",
                                        correo,
                                        Types.VARCHAR
                                )
                                .addValue(
                                        "p_cTokenHash",
                                        tokenHash,
                                        Types.CHAR
                                )
                                .addValue(
                                        "p_nExpiracionMinutos",
                                        expiracionMinutos,
                                        Types.INTEGER
                                )
                );


        List<UsuarioRecuperacion> usuarios =
                (List<UsuarioRecuperacion>)
                        resultado.getOrDefault(
                                "usuario",
                                List.of()
                        );


        return usuarios.stream()
                .findFirst();
    }


    public void consumirToken(
            String tokenHash,
            String passwordHash
    ) {

        paConsumirToken.execute(
                new MapSqlParameterSource()
                        .addValue(
                                "p_cTokenHash",
                                tokenHash,
                                Types.CHAR
                        )
                        .addValue(
                                "p_cPasswordHash",
                                passwordHash,
                                Types.VARCHAR
                        )
        );
    }
}