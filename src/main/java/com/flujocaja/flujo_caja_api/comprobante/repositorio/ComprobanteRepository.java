package com.flujocaja.flujo_caja_api.comprobante.repositorio;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.Types;
import java.time.LocalDate;

@Repository
public class ComprobanteRepository {

    private final SimpleJdbcCall paComprobanteInsUpd;


    public ComprobanteRepository(
            DataSource dataSource
    ) {

        this.paComprobanteInsUpd =
                new SimpleJdbcCall(dataSource)



                        .withProcedureName(
                                "PA_Comprobante_InsUpd"
                        )

                        .withoutProcedureColumnMetaDataAccess()

                        .declareParameters(

                                new SqlParameter(
                                        "nEmpresaId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "nMovimientoId",
                                        Types.BIGINT
                                ),

                                new SqlParameter(
                                        "dFechaComprobante",
                                        Types.DATE
                                ),

                                new SqlParameter(
                                        "cSerieComprobante",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "cNumeroComprobante",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "cDocumentoEmisor",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "cRazonSocialEmisor",
                                        Types.NVARCHAR
                                ),

                                new SqlParameter(
                                        "cArchivoXmlNombre",
                                        Types.NVARCHAR
                                ),

                                new SqlParameter(
                                        "cHashXml",
                                        Types.CHAR
                                )
                        );
    }


    public void guardar(
            Integer empresaId,
            Long movimientoId,

            LocalDate fechaComprobante,

            String serieComprobante,
            String numeroComprobante,

            String documentoEmisor,
            String razonSocialEmisor,

            String archivoXmlNombre,
            String hashXml
    ) {

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()

                        .addValue(
                                "nEmpresaId",
                                empresaId,
                                Types.INTEGER
                        )

                        .addValue(
                                "nMovimientoId",
                                movimientoId,
                                Types.BIGINT
                        )

                        .addValue(
                                "dFechaComprobante",
                                fechaComprobante,
                                Types.DATE
                        )

                        .addValue(
                                "cSerieComprobante",
                                serieComprobante,
                                Types.VARCHAR
                        )

                        .addValue(
                                "cNumeroComprobante",
                                numeroComprobante,
                                Types.VARCHAR
                        )

                        .addValue(
                                "cDocumentoEmisor",
                                documentoEmisor,
                                Types.VARCHAR
                        )

                        .addValue(
                                "cRazonSocialEmisor",
                                razonSocialEmisor,
                                Types.NVARCHAR
                        )

                        .addValue(
                                "cArchivoXmlNombre",
                                archivoXmlNombre,
                                Types.NVARCHAR
                        )

                        .addValue(
                                "cHashXml",
                                hashXml,
                                Types.CHAR
                        );


        paComprobanteInsUpd.execute(
                parametros
        );
    }
}