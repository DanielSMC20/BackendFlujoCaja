package com.flujocaja.flujo_caja_api.cuenta.repositorio;

import com.flujocaja.flujo_caja_api.cuenta.dto.EmpresaActualResponse;
import com.flujocaja.flujo_caja_api.cuenta.dto.MiCuentaResponse;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class CuentaRepository {

    private final SimpleJdbcCall paCuentaSel;

    private final SimpleJdbcCall paCredencialSel;

    private final SimpleJdbcCall paPasswordUpd;


    /* =========================================================
       RECORDS INTERNOS
       ========================================================= */

    private record CuentaBase(

            Long usuarioId,

            String correo,

            String nombres,

            String apellidos,

            String nombreCompleto,

            Boolean correoVerificado,

            Boolean debeCambiarPassword,

            LocalDateTime ultimoAcceso,

            EmpresaActualResponse empresa

    ) {
    }


    private record RolInterno(
            String codigo
    ) {
    }


    public record CredencialInterna(

            String passwordHash,

            Boolean debeCambiarPassword

    ) {
    }


    /* =========================================================
       MAPPERS
       ========================================================= */

    private final RowMapper<CuentaBase> cuentaMapper =
            (rs, rowNum) ->
                    new CuentaBase(

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
                                    "bCorreoVerificado"
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

                            new EmpresaActualResponse(

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

                                    rs.getInt(
                                            "nMonedaBase"
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
                            )
                    );


    private final RowMapper<RolInterno> rolMapper =
            (rs, rowNum) ->
                    new RolInterno(

                            rs.getString(
                                    "cCodigo"
                            )
                    );


    private final RowMapper<CredencialInterna> credencialMapper =
            (rs, rowNum) ->
                    new CredencialInterna(

                            rs.getString(
                                    "cPasswordHash"
                            ),

                            rs.getBoolean(
                                    "bDebeCambiarPassword"
                            )
                    );


    /* =========================================================
       CONSTRUCTOR
       ========================================================= */

    public CuentaRepository(
            DataSource dataSource
    ) {

        this.paCuentaSel =
                new SimpleJdbcCall(dataSource)

                        .withSchemaName(
                                "Seguridad"
                        )

                        .withProcedureName(
                                "PA_Cuenta_Sel"
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
                                "cuenta",
                                cuentaMapper
                        )

                        .returningResultSet(
                                "roles",
                                rolMapper
                        );


        this.paCredencialSel =
                new SimpleJdbcCall(dataSource)

                        .withSchemaName(
                                "Seguridad"
                        )

                        .withProcedureName(
                                "PA_Cuenta_Sel_Credencial"
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
                                "credencial",
                                credencialMapper
                        );


        this.paPasswordUpd =
                new SimpleJdbcCall(dataSource)

                        .withSchemaName(
                                "Seguridad"
                        )

                        .withProcedureName(
                                "PA_Cuenta_Upd_Password"
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
                                ),

                                new SqlParameter(
                                        "cPasswordHash",
                                        Types.VARCHAR
                                )
                        )

                        .returningResultSet(
                                "cuenta",
                                cuentaMapper
                        )

                        .returningResultSet(
                                "roles",
                                rolMapper
                        );
    }


    /* =========================================================
       OBTENER CUENTA
       ========================================================= */

    public MiCuentaResponse obtenerCuenta(
            Integer empresaId,
            Long usuarioId
    ) {

        Map<String, Object> resultado =
                paCuentaSel.execute(

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


        return construirCuenta(
                resultado
        );
    }


    /* =========================================================
       OBTENER CREDENCIAL
       ========================================================= */

    @SuppressWarnings("unchecked")
    public CredencialInterna obtenerCredencial(
            Integer empresaId,
            Long usuarioId
    ) {

        Map<String, Object> resultado =
                paCredencialSel.execute(

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


        List<CredencialInterna> credenciales =
                (List<CredencialInterna>)
                        resultado.getOrDefault(
                                "credencial",
                                List.of()
                        );


        if (credenciales.isEmpty()) {

            throw new IllegalStateException(
                    "No se encontraron las credenciales del usuario."
            );
        }


        return credenciales.get(0);
    }


    /* =========================================================
       CAMBIAR PASSWORD
       ========================================================= */

    public MiCuentaResponse cambiarPassword(
            Integer empresaId,
            Long usuarioId,
            String passwordHash
    ) {

        Map<String, Object> resultado =
                paPasswordUpd.execute(

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

                                .addValue(
                                        "cPasswordHash",
                                        passwordHash,
                                        Types.VARCHAR
                                )
                );


        return construirCuenta(
                resultado
        );
    }


    /* =========================================================
       ARMAR RESPONSE
       ========================================================= */

    @SuppressWarnings("unchecked")
    private MiCuentaResponse construirCuenta(
            Map<String, Object> resultado
    ) {

        List<CuentaBase> cuentas =
                (List<CuentaBase>)
                        resultado.getOrDefault(
                                "cuenta",
                                List.of()
                        );


        if (cuentas.isEmpty()) {

            throw new IllegalStateException(
                    "No se pudo obtener la información de la cuenta."
            );
        }


        CuentaBase cuenta =
                cuentas.get(0);


        List<RolInterno> rolesInternos =
                (List<RolInterno>)
                        resultado.getOrDefault(
                                "roles",
                                List.of()
                        );


        List<String> roles =
                rolesInternos
                        .stream()
                        .map(
                                RolInterno::codigo
                        )
                        .toList();


        return new MiCuentaResponse(

                cuenta.usuarioId(),

                cuenta.correo(),

                cuenta.nombres(),

                cuenta.apellidos(),

                cuenta.nombreCompleto(),

                cuenta.correoVerificado(),

                cuenta.debeCambiarPassword(),

                cuenta.ultimoAcceso(),

                cuenta.empresa(),

                roles
        );
    }
}