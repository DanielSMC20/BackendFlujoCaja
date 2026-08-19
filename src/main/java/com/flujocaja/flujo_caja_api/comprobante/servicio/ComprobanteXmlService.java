package com.flujocaja.flujo_caja_api.comprobante.servicio;

import com.flujocaja.flujo_caja_api.comprobante.dto.ComprobanteXmlResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;

@Service
public class ComprobanteXmlService {

    private static final long TAMANO_MAXIMO =
            5L * 1024 * 1024;


    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','CONTADOR','OPERADOR')"
    )
    public ComprobanteXmlResponse analizar(
            MultipartFile archivo
    ) {

        validarArchivo(
                archivo
        );


        try {

            byte[] contenido =
                    archivo.getBytes();


            String hashXml =
                    calcularHash(
                            contenido
                    );


            Document documento =
                    leerXmlSeguro(
                            contenido
                    );


            XPath xpath =
                    XPathFactory
                            .newInstance()
                            .newXPath();


            String nombreRaiz =
                    documento
                            .getDocumentElement()
                            .getLocalName();


            if (
                    nombreRaiz == null
                            ||
                            nombreRaiz.isBlank()
            ) {

                throw new IllegalArgumentException(
                        "No se pudo identificar el tipo de documento XML."
                );
            }


            /*
             * Por ahora nuestro flujo principal
             * está diseñado para comprobantes Invoice.
             *
             * No confundimos una CreditNote o DebitNote
             * con una factura normal.
             */

            if (
                    !"Invoice".equalsIgnoreCase(
                            nombreRaiz
                    )
            ) {

                throw new IllegalArgumentException(
                        "El XML no corresponde a un comprobante compatible."
                );
            }


            String numeroCompleto =
                    obtenerTexto(
                            xpath,
                            documento,
                            "/*[local-name()='Invoice']" +
                                    "/*[local-name()='ID'][1]"
                    );


            String fechaTexto =
                    obtenerTexto(
                            xpath,
                            documento,
                            "/*[local-name()='Invoice']" +
                                    "/*[local-name()='IssueDate'][1]"
                    );


            String codigoTipo =
                    obtenerTexto(
                            xpath,
                            documento,
                            "/*[local-name()='Invoice']" +
                                    "/*[local-name()='InvoiceTypeCode'][1]"
                    );


            String codigoMoneda =
                    obtenerTexto(
                            xpath,
                            documento,
                            "/*[local-name()='Invoice']" +
                                    "/*[local-name()='DocumentCurrencyCode'][1]"
                    );


            String montoTexto =
                    obtenerTexto(
                            xpath,
                            documento,
                            "/*[local-name()='Invoice']" +
                                    "/*[local-name()='LegalMonetaryTotal']" +
                                    "/*[local-name()='PayableAmount'][1]"
                    );


            /*
             * Algunos XML no traen
             * DocumentCurrencyCode,
             * pero sí currencyID en PayableAmount.
             */

            if (
                    codigoMoneda == null
                            &&
                            montoTexto != null
            ) {

                codigoMoneda =
                        obtenerTexto(
                                xpath,
                                documento,
                                "/*[local-name()='Invoice']" +
                                        "/*[local-name()='LegalMonetaryTotal']" +
                                        "/*[local-name()='PayableAmount'][1]" +
                                        "/@currencyID"
                        );
            }


            /*
             * UBL más reciente.
             */

            String documentoEmisor =
                    obtenerTexto(
                            xpath,
                            documento,
                            "/*[local-name()='Invoice']" +
                                    "/*[local-name()='AccountingSupplierParty']" +
                                    "/*[local-name()='Party']" +
                                    "/*[local-name()='PartyTaxScheme']" +
                                    "/*[local-name()='CompanyID'][1]"
                    );


            /*
             * Compatibilidad con el XML que ya
             * tenemos como ejemplo.
             */

            if (documentoEmisor == null) {

                documentoEmisor =
                        obtenerTexto(
                                xpath,
                                documento,
                                "/*[local-name()='Invoice']" +
                                        "/*[local-name()='AccountingSupplierParty']" +
                                        "/*[local-name()='CustomerAssignedAccountID'][1]"
                        );
            }


            if (documentoEmisor == null) {

                documentoEmisor =
                        obtenerTexto(
                                xpath,
                                documento,
                                "/*[local-name()='Invoice']" +
                                        "/*[local-name()='AccountingSupplierParty']" +
                                        "/*[local-name()='Party']" +
                                        "/*[local-name()='PartyIdentification']" +
                                        "/*[local-name()='ID'][1]"
                        );
            }


            /*
             * Razón social.
             */

            String razonSocial =
                    obtenerTexto(
                            xpath,
                            documento,
                            "/*[local-name()='Invoice']" +
                                    "/*[local-name()='AccountingSupplierParty']" +
                                    "/*[local-name()='Party']" +
                                    "/*[local-name()='PartyLegalEntity']" +
                                    "/*[local-name()='RegistrationName'][1]"
                    );


            if (razonSocial == null) {

                razonSocial =
                        obtenerTexto(
                                xpath,
                                documento,
                                "/*[local-name()='Invoice']" +
                                        "/*[local-name()='AccountingSupplierParty']" +
                                        "/*[local-name()='Party']" +
                                        "/*[local-name()='PartyName']" +
                                        "/*[local-name()='Name'][1]"
                        );
            }


            /*
             * Primera descripción encontrada.
             */

            String descripcion =
                    obtenerTexto(
                            xpath,
                            documento,
                            "/*[local-name()='Invoice']" +
                                    "/*[local-name()='InvoiceLine'][1]" +
                                    "/*[local-name()='Item']" +
                                    "/*[local-name()='Description'][1]"
                    );


            LocalDate fechaEmision =
                    convertirFecha(
                            fechaTexto
                    );


            BigDecimal importeTotal =
                    convertirMonto(
                            montoTexto
                    );


            DocumentoNumero comprobante =
                    separarNumeroComprobante(
                            numeroCompleto
                    );


            Integer tipoComprobante =
                    convertirTipoComprobante(
                            codigoTipo
                    );


            Integer moneda =
                    convertirMoneda(
                            codigoMoneda
                    );


            String nombreArchivo =
                    obtenerNombreSeguro(
                            archivo
                                    .getOriginalFilename()
                    );


            int camposEncontrados =
                    contarCampos(

                            fechaEmision,

                            tipoComprobante,

                            comprobante.serie(),

                            comprobante.numero(),

                            moneda,

                            importeTotal,

                            documentoEmisor,

                            razonSocial,

                            descripcion
                    );


            return new ComprobanteXmlResponse(

                    nombreArchivo,

                    hashXml,

                    fechaEmision,

                    tipoComprobante,

                    codigoTipo,

                    comprobante.serie(),

                    comprobante.numero(),

                    moneda,

                    codigoMoneda,

                    importeTotal,

                    documentoEmisor,

                    razonSocial,

                    descripcion,

                    camposEncontrados
            );


        } catch (IllegalArgumentException ex) {

            throw ex;

        } catch (
                Exception ex
        ) {

            throw new IllegalArgumentException(
                    "No se pudo procesar el archivo XML.",
                    ex
            );
        }
    }


    private void validarArchivo(
            MultipartFile archivo
    ) {

        if (
                archivo == null
                        ||
                        archivo.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un archivo XML."
            );
        }


        if (
                archivo.getSize()
                        >
                        TAMANO_MAXIMO
        ) {

            throw new IllegalArgumentException(
                    "El archivo XML supera el tamaño máximo permitido de 5 MB."
            );
        }


        String nombre =
                obtenerNombreSeguro(
                        archivo.getOriginalFilename()
                );


        if (
                nombre == null
                        ||
                        !nombre
                                .toLowerCase()
                                .endsWith(".xml")
        ) {

            throw new IllegalArgumentException(
                    "El archivo seleccionado debe tener extensión .xml."
            );
        }
    }


    private Document leerXmlSeguro(
            byte[] contenido
    ) throws
            ParserConfigurationException,
            IOException,
            SAXException {

        DocumentBuilderFactory factory =
                DocumentBuilderFactory
                        .newInstance();


        factory.setNamespaceAware(
                true
        );


        /*
         * Seguridad XML.
         */

        factory.setFeature(
                XMLConstants.FEATURE_SECURE_PROCESSING,
                true
        );


        factory.setAttribute(
                XMLConstants.ACCESS_EXTERNAL_DTD,
                ""
        );


        factory.setAttribute(
                XMLConstants.ACCESS_EXTERNAL_SCHEMA,
                ""
        );


        factory.setFeature(
                "http://apache.org/xml/features/disallow-doctype-decl",
                true
        );


        factory.setExpandEntityReferences(
                false
        );


        factory.setXIncludeAware(
                false
        );


        DocumentBuilder builder =
                factory.newDocumentBuilder();


        /*
         * Capa adicional:
         * nunca resolver recursos externos.
         */

        builder.setEntityResolver(
                (
                        publicId,
                        systemId
                ) -> {

                    throw new SAXException(
                            "No se permiten entidades externas en el XML."
                    );
                }
        );


        try (
                ByteArrayInputStream input =
                        new ByteArrayInputStream(
                                contenido
                        )
        ) {

            Document documento =
                    builder.parse(
                            new InputSource(
                                    input
                            )
                    );


            documento
                    .getDocumentElement()
                    .normalize();


            return documento;
        }
    }


    private String obtenerTexto(
            XPath xpath,
            Document documento,
            String expresion
    ) {

        try {

            String resultado =
                    (String)
                            xpath.evaluate(
                                    expresion,
                                    documento,
                                    XPathConstants.STRING
                            );


            if (resultado == null) {
                return null;
            }


            resultado =
                    resultado.trim();


            return resultado.isBlank()
                    ? null
                    : resultado;


        } catch (
                Exception ex
        ) {

            return null;
        }
    }


    private LocalDate convertirFecha(
            String valor
    ) {

        if (valor == null) {
            return null;
        }


        try {

            return LocalDate.parse(
                    valor
            );

        } catch (
                Exception ex
        ) {

            return null;
        }
    }


    private BigDecimal convertirMonto(
            String valor
    ) {

        if (valor == null) {
            return null;
        }


        try {

            return new BigDecimal(
                    valor
            );

        } catch (
                NumberFormatException ex
        ) {

            return null;
        }
    }


    private Integer convertirTipoComprobante(
            String codigo
    ) {

        if (codigo == null) {
            return null;
        }


        return switch (codigo) {

            /*
             * Constante 300
             */

            case "01" -> 1; // Factura

            case "03" -> 2; // Boleta

            default -> null;
        };
    }


    private Integer convertirMoneda(
            String codigo
    ) {

        if (codigo == null) {
            return null;
        }


        return switch (
                codigo.toUpperCase()
                ) {

            /*
             * Constante 400
             */

            case "PEN" -> 1;

            case "USD" -> 2;

            default -> null;
        };
    }


    private DocumentoNumero separarNumeroComprobante(
            String valor
    ) {

        if (
                valor == null
                        ||
                        valor.isBlank()
        ) {

            return new DocumentoNumero(
                    null,
                    null
            );
        }


        int indice =
                valor.indexOf(
                        '-'
                );


        if (
                indice <= 0
                        ||
                        indice ==
                                valor.length() - 1
        ) {

            return new DocumentoNumero(
                    null,
                    valor
            );
        }


        return new DocumentoNumero(

                valor
                        .substring(
                                0,
                                indice
                        )
                        .trim(),

                valor
                        .substring(
                                indice + 1
                        )
                        .trim()
        );
    }


    private String calcularHash(
            byte[] contenido
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );


            return HexFormat
                    .of()
                    .formatHex(
                            digest.digest(
                                    contenido
                            )
                    );


        } catch (
                NoSuchAlgorithmException ex
        ) {

            throw new IllegalStateException(
                    "No se pudo calcular el hash del XML.",
                    ex
            );
        }
    }


    private String obtenerNombreSeguro(
            String nombre
    ) {

        if (
                nombre == null
                        ||
                        nombre.isBlank()
        ) {

            return null;
        }


        return Paths
                .get(nombre)
                .getFileName()
                .toString();
    }


    private int contarCampos(
            Object... valores
    ) {

        int total =
                0;


        for (
                Object valor : valores
        ) {

            if (valor != null) {

                total++;
            }
        }


        return total;
    }


    private record DocumentoNumero(
            String serie,
            String numero
    ) {
    }
}