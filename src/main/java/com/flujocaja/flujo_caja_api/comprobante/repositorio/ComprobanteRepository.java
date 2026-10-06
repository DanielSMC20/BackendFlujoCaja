package com.flujocaja.flujo_caja_api.comprobante.repositorio;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
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
                                        "dFechaVencimiento",
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
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "nBaseImponible",
                                        Types.DECIMAL
                                ),

                                new SqlParameter(
                                        "nIgv",
                                        Types.DECIMAL
                                ),

                                new SqlParameter(
                                        "nInafecto",
                                        Types.DECIMAL
                                ),

                                new SqlParameter(
                                        "nIsc",
                                        Types.DECIMAL
                                ),

                                new SqlParameter(
                                        "nIcbper",
                                        Types.DECIMAL
                                ),

                                new SqlParameter(
                                        "nExonerado",
                                        Types.DECIMAL
                                ),

                                new SqlParameter(
                                        "nPorcentajeIgv",
                                        Types.DECIMAL
                                ),

                                new SqlParameter(
                                        "nTipoCambio",
                                        Types.DECIMAL
                                ),

                                new SqlParameter(
                                        "cArchivoXmlNombre",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "cHashXml",
                                        Types.CHAR
                                ),

                                new SqlParameter(
                                        "nUsuarioId",
                                        Types.BIGINT
                                )
                        );
    }


    public void guardar(
            Integer empresaId,
            Long movimientoId,

            LocalDate fechaComprobante,
            LocalDate fechaVencimiento,

            String serieComprobante,
            String numeroComprobante,

            String documentoEmisor,
            String razonSocialEmisor,

            BigDecimal baseImponible,
            BigDecimal igv,
            BigDecimal inafecto,
            BigDecimal isc,
            BigDecimal icbper,
            BigDecimal exonerado,

            BigDecimal porcentajeIgv,
            BigDecimal tipoCambio,

            String archivoXmlNombre,
            String hashXml,

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
                                "dFechaVencimiento",
                                fechaVencimiento,
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
                                Types.VARCHAR
                        )

                        .addValue(
                                "nBaseImponible",
                                baseImponible,
                                Types.DECIMAL
                        )

                        .addValue(
                                "nIgv",
                                igv,
                                Types.DECIMAL
                        )

                        .addValue(
                                "nInafecto",
                                inafecto,
                                Types.DECIMAL
                        )

                        .addValue(
                                "nIsc",
                                isc,
                                Types.DECIMAL
                        )

                        .addValue(
                                "nIcbper",
                                icbper,
                                Types.DECIMAL
                        )

                        .addValue(
                                "nExonerado",
                                exonerado,
                                Types.DECIMAL
                        )

                        .addValue(
                                "nPorcentajeIgv",
                                porcentajeIgv,
                                Types.DECIMAL
                        )

                        .addValue(
                                "nTipoCambio",
                                tipoCambio,
                                Types.DECIMAL
                        )

                        .addValue(
                                "cArchivoXmlNombre",
                                archivoXmlNombre,
                                Types.VARCHAR
                        )

                        .addValue(
                                "cHashXml",
                                hashXml,
                                Types.CHAR
                        )

                        .addValue(
                                "nUsuarioId",
                                usuarioId,
                                Types.BIGINT
                        );


        paComprobanteInsUpd.execute(
                parametros
        );
    }
}