package com.flujocaja.flujo_caja_api.usuario.repositorio;

import com.flujocaja.flujo_caja_api.usuario.dto.RolGestionResponse;
import com.flujocaja.flujo_caja_api.usuario.dto.UsuarioEmpresaResponse;

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
public class UsuarioRepository {

    private final SimpleJdbcCall paListar;

    private final SimpleJdbcCall paObtener;

    private final SimpleJdbcCall paCrear;

    private final SimpleJdbcCall paActualizar;

    private final SimpleJdbcCall paEstado;

    private final SimpleJdbcCall paResetPassword;

    private final SimpleJdbcCall paRoles;


    private final RowMapper<UsuarioEmpresaResponse> usuarioMapper =
            (rs, rowNum) ->
                    new UsuarioEmpresaResponse(

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
                                    "cNombreCompleto"
                            ),

                            rs.getBoolean(
                                    "bActivo"
                            ),

                            rs.getObject(
                                    "nRolId",
                                    Integer.class
                            ),

                            rs.getString(
                                    "cRolCodigo"
                            ),

                            rs.getString(
                                    "cRolNombre"
                            ),

                            rs.getBoolean(
                                    "bDebeCambiarPassword"
                            ),

                            rs.getTimestamp(
                                    "dUltimoAcceso"
                            ) == null
                                    ? null
                                    : rs.getTimestamp(
                                    "dUltimoAcceso"
                            ).toLocalDateTime(),

                            rs.getTimestamp(
                                    "dFechaRegistro"
                            ) == null
                                    ? null
                                    : rs.getTimestamp(
                                    "dFechaRegistro"
                            ).toLocalDateTime(),

                            rs.getTimestamp(
                                    "dFechaBaja"
                            ) == null
                                    ? null
                                    : rs.getTimestamp(
                                    "dFechaBaja"
                            ).toLocalDateTime()
                    );


    private final RowMapper<RolGestionResponse> rolMapper =
            (rs, rowNum) ->
                    new RolGestionResponse(

                            rs.getInt(
                                    "nRolId"
                            ),

                            rs.getString(
                                    "cCodigo"
                            ),

                            rs.getString(
                                    "cNombre"
                            ),

                            rs.getString(
                                    "cDescripcion"
                            )
                    );


    public UsuarioRepository(
            DataSource dataSource
    ) {

        this.paListar =
                new SimpleJdbcCall(dataSource)

                        .withProcedureName(
                                "PA_UsuarioEmpresa_Sel"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "bSoloActivos",
                                        Types.BIT
                                )
                        )

                        .returningResultSet(
                                "usuarios",
                                usuarioMapper
                        );


        this.paObtener =
                new SimpleJdbcCall(dataSource)


                        .withProcedureName(
                                "PA_UsuarioEmpresa_Sel_Id"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nUsuarioId",
                                        Types.BIGINT
                                )
                        )

                        .returningResultSet(
                                "usuario",
                                usuarioMapper
                        );


        this.paCrear =
                new SimpleJdbcCall(dataSource)

                        .withProcedureName(
                                "PA_UsuarioEmpresa_Ins"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nUsuarioAdministradorId",
                                        Types.BIGINT
                                ),

                                new SqlParameter(
                                        "cCorreo",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "cNombres",
                                        Types.NVARCHAR
                                ),

                                new SqlParameter(
                                        "cApellidos",
                                        Types.NVARCHAR
                                ),

                                new SqlParameter(
                                        "cPasswordHash",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "nRolId",
                                        Types.SMALLINT
                                )
                        )

                        .returningResultSet(
                                "usuario",
                                usuarioMapper
                        );


        this.paActualizar =
                new SimpleJdbcCall(dataSource)

                        .withProcedureName(
                                "PA_UsuarioEmpresa_Upd"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nUsuarioAdministradorId",
                                        Types.BIGINT
                                ),

                                new SqlParameter(
                                        "nUsuarioId",
                                        Types.BIGINT
                                ),

                                new SqlParameter(
                                        "cNombres",
                                        Types.NVARCHAR
                                ),

                                new SqlParameter(
                                        "cApellidos",
                                        Types.NVARCHAR
                                ),

                                new SqlParameter(
                                        "nRolId",
                                        Types.SMALLINT
                                )
                        )

                        .returningResultSet(
                                "usuario",
                                usuarioMapper
                        );


        this.paEstado =
                new SimpleJdbcCall(dataSource)


                        .withProcedureName(
                                "PA_UsuarioEmpresa_Upd_Estado"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nUsuarioAdministradorId",
                                        Types.BIGINT
                                ),

                                new SqlParameter(
                                        "nUsuarioId",
                                        Types.BIGINT
                                ),

                                new SqlParameter(
                                        "bActivo",
                                        Types.BIT
                                )
                        )

                        .returningResultSet(
                                "usuario",
                                usuarioMapper
                        );


        this.paResetPassword =
                new SimpleJdbcCall(dataSource)

                        .withProcedureName(
                                "PA_UsuarioCredencial_Upd_ResetPassword"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nUsuarioAdministradorId",
                                        Types.BIGINT
                                ),

                                new SqlParameter(
                                        "nUsuarioId",
                                        Types.BIGINT
                                ),

                                new SqlParameter(
                                        "cPasswordHash",
                                        Types.VARCHAR
                                )
                        )

                        .returningResultSet(
                                "usuario",
                                usuarioMapper
                        );


        this.paRoles =
                new SimpleJdbcCall(dataSource)
                        .withProcedureName(
                                "PA_Rol_Sel_Gestion"
                        )
                        .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(
                                new SqlParameter(
                                        "bActivo",
                                        Types.TINYINT
                                ),
                                new SqlParameter(
                                        "cBuscar",
                                        Types.VARCHAR
                                )
                        )
                        .returningResultSet(
                                "roles",
                                rolMapper
                        );
    }


    @SuppressWarnings("unchecked")
    public List<UsuarioEmpresaResponse> listar(
            Integer empresaId,
            Boolean soloActivos
    ) {

        Map<String, Object> resultado =
                paListar.execute(

                        new MapSqlParameterSource()

                                .addValue(
                                        "nEmpresaId",
                                        empresaId,
                                        Types.INTEGER
                                )

                                .addValue(
                                        "bSoloActivos",
                                        soloActivos,
                                        Types.BIT
                                )
                );


        return (List<UsuarioEmpresaResponse>)
                resultado.getOrDefault(
                        "usuarios",
                        List.of()
                );
    }


    public Optional<UsuarioEmpresaResponse> obtener(
            Integer empresaId,
            Long usuarioId
    ) {

        Map<String, Object> resultado =
                paObtener.execute(

                        new MapSqlParameterSource()

                                .addValue(
                                        "nEmpresaId",
                                        empresaId,
                                        Types.INTEGER
                                )

                                .addValue(
                                        "nUsuarioId",
                                        usuarioId,
                                        Types.BIGINT
                                )
                );


        return obtenerUsuario(
                resultado
        );
    }


    public UsuarioEmpresaResponse crear(
            Integer empresaId,
            Long administradorId,

            String correo,
            String nombres,
            String apellidos,

            String passwordHash,

            Integer rolId
    ) {

        Map<String, Object> resultado =
                paCrear.execute(

                        new MapSqlParameterSource()

                                .addValue(
                                        "nEmpresaId",
                                        empresaId
                                )

                                .addValue(
                                        "nUsuarioAdministradorId",
                                        administradorId
                                )

                                .addValue(
                                        "cCorreo",
                                        correo
                                )

                                .addValue(
                                        "cNombres",
                                        nombres
                                )

                                .addValue(
                                        "cApellidos",
                                        apellidos
                                )

                                .addValue(
                                        "cPasswordHash",
                                        passwordHash
                                )

                                .addValue(
                                        "nRolId",
                                        rolId
                                )
                );


        return obtenerUsuarioObligatorio(
                resultado
        );
    }


    public UsuarioEmpresaResponse actualizar(
            Integer empresaId,
            Long administradorId,
            Long usuarioId,

            String nombres,
            String apellidos,

            Integer rolId
    ) {

        Map<String, Object> resultado =
                paActualizar.execute(

                        new MapSqlParameterSource()

                                .addValue(
                                        "nEmpresaId",
                                        empresaId
                                )

                                .addValue(
                                        "nUsuarioAdministradorId",
                                        administradorId
                                )

                                .addValue(
                                        "nUsuarioId",
                                        usuarioId
                                )

                                .addValue(
                                        "cNombres",
                                        nombres
                                )

                                .addValue(
                                        "cApellidos",
                                        apellidos
                                )

                                .addValue(
                                        "nRolId",
                                        rolId
                                )
                );


        return obtenerUsuarioObligatorio(
                resultado
        );
    }


    public UsuarioEmpresaResponse cambiarEstado(
            Integer empresaId,
            Long administradorId,
            Long usuarioId,
            Boolean activo
    ) {

        Map<String, Object> resultado =
                paEstado.execute(

                        new MapSqlParameterSource()

                                .addValue(
                                        "nEmpresaId",
                                        empresaId
                                )

                                .addValue(
                                        "nUsuarioAdministradorId",
                                        administradorId
                                )

                                .addValue(
                                        "nUsuarioId",
                                        usuarioId
                                )

                                .addValue(
                                        "bActivo",
                                        activo
                                )
                );


        return obtenerUsuarioObligatorio(
                resultado
        );
    }


    public UsuarioEmpresaResponse resetPassword(
            Integer empresaId,
            Long administradorId,
            Long usuarioId,
            String passwordHash
    ) {

        Map<String, Object> resultado =
                paResetPassword.execute(

                        new MapSqlParameterSource()

                                .addValue(
                                        "nEmpresaId",
                                        empresaId
                                )

                                .addValue(
                                        "nUsuarioAdministradorId",
                                        administradorId
                                )

                                .addValue(
                                        "nUsuarioId",
                                        usuarioId
                                )

                                .addValue(
                                        "cPasswordHash",
                                        passwordHash
                                )
                );


        return obtenerUsuarioObligatorio(
                resultado
        );
    }


    @SuppressWarnings("unchecked")
    public List<RolGestionResponse> listarRoles() {

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue(
                                "bActivo",
                                1,
                                Types.TINYINT
                        )
                        .addValue(
                                "cBuscar",
                                null,
                                Types.VARCHAR
                        );

        Map<String, Object> resultado =
                paRoles.execute(
                        parametros
                );

        return (List<RolGestionResponse>)
                resultado.getOrDefault(
                        "roles",
                        List.of()
                );
    }


    @SuppressWarnings("unchecked")
    private Optional<UsuarioEmpresaResponse> obtenerUsuario(
            Map<String, Object> resultado
    ) {

        List<UsuarioEmpresaResponse> usuarios =
                (List<UsuarioEmpresaResponse>)
                        resultado.getOrDefault(
                                "usuario",
                                List.of()
                        );


        return usuarios
                .stream()
                .findFirst();
    }


    private UsuarioEmpresaResponse obtenerUsuarioObligatorio(
            Map<String, Object> resultado
    ) {

        return obtenerUsuario(
                resultado
        )
                .orElseThrow(

                        () ->
                                new IllegalStateException(
                                        "El procedimiento no devolvió el usuario procesado."
                                )
                );
    }
}