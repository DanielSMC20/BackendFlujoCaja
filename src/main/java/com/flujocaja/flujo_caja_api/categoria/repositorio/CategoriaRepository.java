package com.flujocaja.flujo_caja_api.categoria.repositorio;

import com.flujocaja.flujo_caja_api.categoria.dto.CategoriaResponse;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class CategoriaRepository {

    private final SimpleJdbcCall paCategoriaSel;
    private final SimpleJdbcCall paCategoriaIns;
    private final SimpleJdbcCall paCategoriaUpd;


    private final RowMapper<CategoriaResponse> categoriaMapper =
            (rs, rowNum) ->
                    new CategoriaResponse(

                            rs.getInt(
                                    "nCategoriaMovimientoId"
                            ),

                            rs.getInt(
                                    "nTipoMovimiento"
                            ),

                            rs.getString(
                                    "cNombre"
                            ),

                            rs.getString(
                                    "cDescripcion"
                            ),

                            rs.getBoolean(
                                    "bActivo"
                            )
                    );


    public CategoriaRepository(
            DataSource dataSource
    ) {

        /* ==========================
           LISTAR
           ========================== */

        paCategoriaSel =
                new SimpleJdbcCall(dataSource)

                        .withSchemaName(
                                "Finanzas"
                        )

                        .withProcedureName(
                                "PA_CategoriaMovimiento_Sel"
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
                                        "bSoloActivos",
                                        Types.BIT
                                )
                        )

                        .returningResultSet(
                                "categorias",
                                categoriaMapper
                        );


        /* ==========================
           INSERTAR
           ========================== */

        paCategoriaIns =
                new SimpleJdbcCall(dataSource)

                        .withSchemaName(
                                "Finanzas"
                        )

                        .withProcedureName(
                                "PA_CategoriaMovimiento_Ins"
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
                                        "cNombre",
                                        Types.NVARCHAR
                                ),

                                new SqlParameter(
                                        "cDescripcion",
                                        Types.NVARCHAR
                                ),

                                new SqlParameter(
                                        "nUsuarioRegistroId",
                                        Types.BIGINT
                                )
                        )

                        .returningResultSet(
                                "categoria",
                                categoriaMapper
                        );


        /* ==========================
           ACTUALIZAR
           ========================== */

        paCategoriaUpd =
                new SimpleJdbcCall(dataSource)

                        .withSchemaName(
                                "Finanzas"
                        )

                        .withProcedureName(
                                "PA_CategoriaMovimiento_Upd"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nCategoriaMovimientoId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "cNombre",
                                        Types.NVARCHAR
                                ),

                                new SqlParameter(
                                        "cDescripcion",
                                        Types.NVARCHAR
                                ),

                                new SqlParameter(
                                        "bActivo",
                                        Types.BIT
                                ),

                                new SqlParameter(
                                        "nUsuarioModificacionId",
                                        Types.BIGINT
                                )
                        )

                        .returningResultSet(
                                "categoria",
                                categoriaMapper
                        );
    }


    @SuppressWarnings("unchecked")
    public List<CategoriaResponse> listar(
            Integer empresaId,
            Integer tipoMovimiento,
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
                                "bSoloActivos",
                                soloActivos,
                                Types.BIT
                        );


        Map<String, Object> resultado =
                paCategoriaSel.execute(
                        parametros
                );


        return (List<CategoriaResponse>)
                resultado.getOrDefault(
                        "categorias",
                        List.of()
                );
    }


    @SuppressWarnings("unchecked")
    public CategoriaResponse registrar(
            Integer empresaId,
            Integer tipoMovimiento,
            String nombre,
            String descripcion,
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
                                "cNombre",
                                nombre,
                                Types.NVARCHAR
                        )

                        .addValue(
                                "cDescripcion",
                                descripcion,
                                Types.NVARCHAR
                        )

                        .addValue(
                                "nUsuarioRegistroId",
                                usuarioId,
                                Types.BIGINT
                        );


        Map<String, Object> resultado =
                paCategoriaIns.execute(
                        parametros
                );


        List<CategoriaResponse> categorias =
                (List<CategoriaResponse>)
                        resultado.getOrDefault(
                                "categoria",
                                List.of()
                        );


        if (categorias.isEmpty()) {

            throw new IllegalStateException(
                    "No se pudo registrar la categoría."
            );
        }


        return categorias.get(0);
    }


    @SuppressWarnings("unchecked")
    public CategoriaResponse actualizar(
            Integer empresaId,
            Integer categoriaId,
            String nombre,
            String descripcion,
            Boolean activo,
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
                                "nCategoriaMovimientoId",
                                categoriaId,
                                Types.INTEGER
                        )

                        .addValue(
                                "cNombre",
                                nombre,
                                Types.NVARCHAR
                        )

                        .addValue(
                                "cDescripcion",
                                descripcion,
                                Types.NVARCHAR
                        )

                        .addValue(
                                "bActivo",
                                activo,
                                Types.BIT
                        )

                        .addValue(
                                "nUsuarioModificacionId",
                                usuarioId,
                                Types.BIGINT
                        );


        Map<String, Object> resultado =
                paCategoriaUpd.execute(
                        parametros
                );


        List<CategoriaResponse> categorias =
                (List<CategoriaResponse>)
                        resultado.getOrDefault(
                                "categoria",
                                List.of()
                        );


        if (categorias.isEmpty()) {

            throw new IllegalStateException(
                    "No se pudo actualizar la categoría."
            );
        }


        return categorias.get(0);
    }
}