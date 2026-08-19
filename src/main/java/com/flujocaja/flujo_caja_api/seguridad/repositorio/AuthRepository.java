package com.flujocaja.flujo_caja_api.seguridad.repositorio;

import com.flujocaja.flujo_caja_api.seguridad.modelo.EmpresaLogin;
import com.flujocaja.flujo_caja_api.seguridad.modelo.UsuarioLogin;

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
public class AuthRepository {

    private final SimpleJdbcCall paLogin;

    private final SimpleJdbcCall paLoginFallido;

    private final SimpleJdbcCall paLoginExitoso;


    private record UsuarioBase(

            Long usuarioId,

            String correo,

            String nombres,

            String apellidos,

            String passwordHash,

            Integer intentosFallidos,

            java.time.LocalDateTime bloqueadoHasta,

            Boolean debeCambiarPassword,

            Boolean correoVerificado,

            Integer cantidadEmpresasActivas,

            EmpresaLogin empresa

    ) {
    }


    private record RolLogin(
            String codigo
    ) {
    }


    private final RowMapper<UsuarioBase> usuarioMapper =
            (rs, rowNum) -> {

                Integer empresaId =
                        rs.getObject(
                                "nEmpresaId",
                                Integer.class
                        );


                EmpresaLogin empresa =
                        null;


                if (empresaId != null) {

                    empresa =
                            new EmpresaLogin(

                                    empresaId,

                                    rs.getString(
                                            "cRuc"
                                    ),

                                    rs.getString(
                                            "cRazonSocial"
                                    ),

                                    rs.getString(
                                            "cNombreComercial"
                                    ),

                                    rs.getObject(
                                            "nMonedaBase",
                                            Integer.class
                                    ),

                                    rs.getString(
                                            "cMonedaBase"
                                    ),

                                    rs.getString(
                                            "cMonedaBaseAbreviatura"
                                    ),

                                    rs.getString(
                                            "cZonaHoraria"
                                    )
                            );
                }


                java.sql.Timestamp bloqueado =
                        rs.getTimestamp(
                                "dBloqueadoHasta"
                        );


                return new UsuarioBase(

                        rs.getLong(
                                "nUsuarioId"
                        ),

                        rs.getString(
                                "cCorreo"
                        ),

                        rs.getString(
                                "cNombres"
                        ),

                        rs.getString(
                                "cApellidos"
                        ),

                        rs.getString(
                                "cPasswordHash"
                        ),

                        rs.getInt(
                                "nIntentosFallidos"
                        ),

                        bloqueado == null
                                ? null
                                : bloqueado.toLocalDateTime(),

                        rs.getBoolean(
                                "bDebeCambiarPassword"
                        ),

                        rs.getBoolean(
                                "bCorreoVerificado"
                        ),

                        rs.getInt(
                                "nCantidadEmpresasActivas"
                        ),

                        empresa
                );
            };


    private final RowMapper<RolLogin> rolMapper =
            (rs, rowNum) ->
                    new RolLogin(

                            rs.getString(
                                    "cCodigo"
                            )
                    );


    public AuthRepository(
            DataSource dataSource
    ) {

        this.paLogin =
                new SimpleJdbcCall(dataSource)


                        .withProcedureName(
                                "PA_Usuario_Sel_Login"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "cCorreo",
                                        Types.VARCHAR
                                )
                        )

                        .returningResultSet(
                                "usuario",
                                usuarioMapper
                        )

                        .returningResultSet(
                                "roles",
                                rolMapper
                        );


        this.paLoginFallido =
                new SimpleJdbcCall(dataSource)


                        .withProcedureName(
                                "PA_Usuario_Upd_LoginFallido"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "nUsuarioId",
                                        Types.BIGINT
                                )
                        );


        this.paLoginExitoso =
                new SimpleJdbcCall(dataSource)

                        .withProcedureName(
                                "PA_Usuario_Upd_LoginExitoso"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "nUsuarioId",
                                        Types.BIGINT
                                )
                        );
    }


    @SuppressWarnings("unchecked")
    public Optional<UsuarioLogin> buscarPorCorreo(
            String correo
    ) {

        Map<String, Object> resultado =
                paLogin.execute(

                        new MapSqlParameterSource()

                                .addValue(
                                        "cCorreo",
                                        correo,
                                        Types.VARCHAR
                                )
                );


        List<UsuarioBase> usuarios =
                (List<UsuarioBase>)
                        resultado.getOrDefault(
                                "usuario",
                                List.of()
                        );


        if (usuarios.isEmpty()) {

            return Optional.empty();
        }


        UsuarioBase usuario =
                usuarios.get(0);


        List<RolLogin> rolesDb =
                (List<RolLogin>)
                        resultado.getOrDefault(
                                "roles",
                                List.of()
                        );


        List<String> roles =
                rolesDb
                        .stream()
                        .map(
                                RolLogin::codigo
                        )
                        .distinct()
                        .toList();


        return Optional.of(

                new UsuarioLogin(

                        usuario.usuarioId(),

                        usuario.correo(),

                        usuario.nombres(),

                        usuario.apellidos(),

                        usuario.passwordHash(),

                        usuario.intentosFallidos(),

                        usuario.bloqueadoHasta(),

                        usuario.debeCambiarPassword(),

                        usuario.correoVerificado(),

                        usuario.cantidadEmpresasActivas(),

                        usuario.empresa(),

                        roles
                )
        );
    }


    public void registrarLoginFallido(
            Long usuarioId
    ) {

        paLoginFallido.execute(

                new MapSqlParameterSource()

                        .addValue(
                                "nUsuarioId",
                                usuarioId,
                                Types.BIGINT
                        )
        );
    }


    public void registrarLoginExitoso(
            Long usuarioId
    ) {

        paLoginExitoso.execute(

                new MapSqlParameterSource()

                        .addValue(
                                "nUsuarioId",
                                usuarioId,
                                Types.BIGINT
                        )
        );
    }
}