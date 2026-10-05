package com.flujocaja.flujo_caja_api.plataforma.repositorio;

import com.flujocaja.flujo_caja_api.plataforma.dto.EmpresaPlataformaResponse;
import com.flujocaja.flujo_caja_api.plataforma.modelo.AdministradorPlataformaLogin;

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
public class PlataformaRepository {

    private final SimpleJdbcCall paLogin;

    private final SimpleJdbcCall paLoginFallido;

    private final SimpleJdbcCall paLoginExitoso;

    private final SimpleJdbcCall paListarEmpresas;

    private final SimpleJdbcCall paObtenerEmpresa;

    private final SimpleJdbcCall paCrearEmpresa;

    private final SimpleJdbcCall paActualizarEmpresa;


    private final RowMapper<AdministradorPlataformaLogin>
            administradorMapper =

            (rs, rowNum) -> {

                var bloqueadoHasta =
                        rs.getTimestamp(
                                "dBloqueadoHasta"
                        );


                return new AdministradorPlataformaLogin(

                        rs.getLong(
                                "nAdministradorPlataformaId"
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

                        bloqueadoHasta == null
                                ? null
                                : bloqueadoHasta.toLocalDateTime()
                );
            };


    private final RowMapper<EmpresaPlataformaResponse>
            empresaMapper =

            (rs, rowNum) -> {

                var fechaRegistro =
                        rs.getTimestamp(
                                "dFechaRegistro"
                        );


                return new EmpresaPlataformaResponse(

                        rs.getInt(
                                "nEmpresaId"
                        ),

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
                                "cMonedaDescripcion"
                        ),

                        rs.getString(
                                "cMonedaAbreviatura"
                        ),

                        rs.getString(
                                "cZonaHoraria"
                        ),

                        rs.getBoolean(
                                "bActivo"
                        ),

                        fechaRegistro == null
                                ? null
                                : fechaRegistro.toLocalDateTime(),


                        rs.getObject(
                                "nAdministradorUsuarioId",
                                Long.class
                        ),

                        rs.getString(
                                "cAdministradorNombres"
                        ),

                        rs.getString(
                                "cAdministradorApellidos"
                        ),

                        rs.getString(
                                "cAdministradorNombreCompleto"
                        ),

                        rs.getString(
                                "cAdministradorCorreo"
                        )
                );
            };


    public PlataformaRepository(
            DataSource dataSource
    ) {

        paLogin =
                new SimpleJdbcCall(
                        dataSource
                )

                        .withProcedureName(
                                "PA_AdministradorPlataforma_Sel_Login"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "p_cCorreo",
                                        Types.VARCHAR
                                )
                        )

                        .returningResultSet(
                                "administrador",
                                administradorMapper
                        );


        paLoginFallido =
                new SimpleJdbcCall(
                        dataSource
                )

                        .withProcedureName(
                                "PA_AdministradorPlataforma_Upd_LoginFallido"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "p_nAdministradorPlataformaId",
                                        Types.BIGINT
                                )
                        );


        paLoginExitoso =
                new SimpleJdbcCall(
                        dataSource
                )

                        .withProcedureName(
                                "PA_AdministradorPlataforma_Upd_LoginExitoso"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "p_nAdministradorPlataformaId",
                                        Types.BIGINT
                                )
                        );


        paListarEmpresas =
                new SimpleJdbcCall(
                        dataSource
                )

                        .withProcedureName(
                                "PA_Plataforma_Sel_Empresas"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "p_cBusqueda",
                                        Types.VARCHAR
                                )
                        )

                        .returningResultSet(
                                "empresas",
                                empresaMapper
                        );


        paObtenerEmpresa =
                new SimpleJdbcCall(
                        dataSource
                )

                        .withProcedureName(
                                "PA_Plataforma_Sel_Empresa_Id"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "p_nEmpresaId",
                                        Types.INTEGER
                                )
                        )

                        .returningResultSet(
                                "empresa",
                                empresaMapper
                        );


        /*
         * Reutilizamos el PA que ya tienes.
         */
        paCrearEmpresa =
                new SimpleJdbcCall(
                        dataSource
                )

                        .withProcedureName(
                                "PA_Usuario_Ins_RegistroEmpresa"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "p_cRuc",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "p_cRazonSocial",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "p_cNombreComercial",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "p_nMonedaBase",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "p_cZonaHoraria",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "p_cCorreo",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "p_cNombres",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "p_cApellidos",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "p_cPasswordHash",
                                        Types.VARCHAR
                                )
                        )

                        .returningResultSet(

                                "empresaCreada",

                                (rs, rowNum) ->
                                        rs.getInt(
                                                "nEmpresaId"
                                        )
                        );


        paActualizarEmpresa =
                new SimpleJdbcCall(
                        dataSource
                )

                        .withProcedureName(
                                "PA_Plataforma_Upd_Empresa"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "p_nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "p_cRuc",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "p_cRazonSocial",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "p_cNombreComercial",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "p_nMonedaBase",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "p_cZonaHoraria",
                                        Types.VARCHAR
                                )
                        );
    }


    @SuppressWarnings("unchecked")
    public Optional<AdministradorPlataformaLogin>
    buscarAdministradorPorCorreo(
            String correo
    ) {

        Map<String, Object> resultado =
                paLogin.execute(

                        new MapSqlParameterSource()

                                .addValue(
                                        "p_cCorreo",
                                        correo,
                                        Types.VARCHAR
                                )
                );


        List<AdministradorPlataformaLogin> lista =
                (List<AdministradorPlataformaLogin>)
                        resultado.getOrDefault(
                                "administrador",
                                List.of()
                        );


        return lista
                .stream()
                .findFirst();
    }


    public void registrarLoginFallido(
            Long administradorId
    ) {

        paLoginFallido.execute(

                new MapSqlParameterSource()

                        .addValue(
                                "p_nAdministradorPlataformaId",
                                administradorId,
                                Types.BIGINT
                        )
        );
    }


    public void registrarLoginExitoso(
            Long administradorId
    ) {

        paLoginExitoso.execute(

                new MapSqlParameterSource()

                        .addValue(
                                "p_nAdministradorPlataformaId",
                                administradorId,
                                Types.BIGINT
                        )
        );
    }


    @SuppressWarnings("unchecked")
    public List<EmpresaPlataformaResponse>
    listarEmpresas(
            String busqueda
    ) {

        Map<String, Object> resultado =
                paListarEmpresas.execute(

                        new MapSqlParameterSource()

                                .addValue(
                                        "p_cBusqueda",
                                        busqueda,
                                        Types.VARCHAR
                                )
                );


        return (List<EmpresaPlataformaResponse>)
                resultado.getOrDefault(
                        "empresas",
                        List.of()
                );
    }


    @SuppressWarnings("unchecked")
    public Optional<EmpresaPlataformaResponse>
    obtenerEmpresa(
            Integer empresaId
    ) {

        Map<String, Object> resultado =
                paObtenerEmpresa.execute(

                        new MapSqlParameterSource()

                                .addValue(
                                        "p_nEmpresaId",
                                        empresaId,
                                        Types.INTEGER
                                )
                );


        List<EmpresaPlataformaResponse> empresas =
                (List<EmpresaPlataformaResponse>)
                        resultado.getOrDefault(
                                "empresa",
                                List.of()
                        );


        return empresas
                .stream()
                .findFirst();
    }


    @SuppressWarnings("unchecked")
    public Integer crearEmpresa(

            String ruc,

            String razonSocial,

            String nombreComercial,

            Integer monedaBase,

            String zonaHoraria,

            String correo,

            String nombres,

            String apellidos,

            String passwordHash

    ) {

        Map<String, Object> resultado =
                paCrearEmpresa.execute(

                        new MapSqlParameterSource()

                                .addValue(
                                        "p_cRuc",
                                        ruc
                                )

                                .addValue(
                                        "p_cRazonSocial",
                                        razonSocial
                                )

                                .addValue(
                                        "p_cNombreComercial",
                                        nombreComercial
                                )

                                .addValue(
                                        "p_nMonedaBase",
                                        monedaBase
                                )

                                .addValue(
                                        "p_cZonaHoraria",
                                        zonaHoraria
                                )

                                .addValue(
                                        "p_cCorreo",
                                        correo
                                )

                                .addValue(
                                        "p_cNombres",
                                        nombres
                                )

                                .addValue(
                                        "p_cApellidos",
                                        apellidos
                                )

                                .addValue(
                                        "p_cPasswordHash",
                                        passwordHash
                                )
                );


        List<Integer> ids =
                (List<Integer>)
                        resultado.getOrDefault(
                                "empresaCreada",
                                List.of()
                        );


        if (ids.isEmpty()) {

            throw new IllegalStateException(
                    "No se obtuvo el identificador de la empresa."
            );
        }


        return ids.getFirst();
    }


    public void actualizarEmpresa(

            Integer empresaId,

            String ruc,

            String razonSocial,

            String nombreComercial,

            Integer monedaBase,

            String zonaHoraria

    ) {

        paActualizarEmpresa.execute(

                new MapSqlParameterSource()

                        .addValue(
                                "p_nEmpresaId",
                                empresaId
                        )

                        .addValue(
                                "p_cRuc",
                                ruc
                        )

                        .addValue(
                                "p_cRazonSocial",
                                razonSocial
                        )

                        .addValue(
                                "p_cNombreComercial",
                                nombreComercial
                        )

                        .addValue(
                                "p_nMonedaBase",
                                monedaBase
                        )

                        .addValue(
                                "p_cZonaHoraria",
                                zonaHoraria
                        )
        );
    }
}