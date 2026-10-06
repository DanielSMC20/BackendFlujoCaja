package com.flujocaja.flujo_caja_api.comprobante.servicio;

import com.flujocaja.flujo_caja_api.comprobante.dto.ComprobanteXmlResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ComprobanteXmlService {

    private static final long TAMANO_MAXIMO =
            5L * 1024 * 1024;

    private static final BigDecimal CERO =
            BigDecimal.ZERO;

    /*
     * Códigos tributarios SUNAT utilizados en UBL.
     */
    private static final String CODIGO_IGV = "1000";
    private static final String CODIGO_ISC = "2000";
    private static final String CODIGO_ICBPER = "7152";
    private static final String CODIGO_EXONERADO = "9997";
    private static final String CODIGO_INAFECTO = "9998";


    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','CONTADOR','OPERADOR')"
    )
    public ComprobanteXmlResponse analizar(
            MultipartFile archivo
    ) {

        validarArchivo(archivo);

        try {

            byte[] contenido =
                    archivo.getBytes();

            String hashXml =
                    calcularHash(contenido);

            Document documento =
                    leerXmlSeguro(contenido);

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
             * Por ahora el flujo está diseñado para Invoice.
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


            /* =====================================================
               DATOS GENERALES DEL COMPROBANTE
               ===================================================== */

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
             * Algunos XML no incluyen DocumentCurrencyCode,
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


            /* =====================================================
               FECHA DE VENCIMIENTO
               ===================================================== */

            String fechaVencimientoTexto =
                    obtenerFechaVencimiento(
                            xpath,
                            documento
                    );


            /* =====================================================
               EMISOR
               ===================================================== */

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
             * Compatibilidad con XML anteriores.
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


            /* =====================================================
               RAZÓN SOCIAL
               ===================================================== */

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


            /* =====================================================
               DATOS TRIBUTARIOS
               ===================================================== */

            DatosTributarios datosTributarios =
                    obtenerDatosTributarios(
                            xpath,
                            documento
                    );


            /* =====================================================
               GLOSA / DESCRIPCIÓN

               Se obtienen las descripciones de todos los
               InvoiceLine y se eliminan duplicados.
               ===================================================== */

            String descripcion =
                    obtenerGlosa(
                            xpath,
                            documento
                    );


            /* =====================================================
               CONVERSIONES
               ===================================================== */

            LocalDate fechaEmision =
                    convertirFecha(
                            fechaTexto
                    );

            LocalDate fechaVencimiento =
                    convertirFecha(
                            fechaVencimientoTexto
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


            /* =====================================================
               TIPO DE CAMBIO

               Para PEN el valor es 1.
               Para moneda extranjera solo se utiliza el dato
               cuando realmente se encuentra informado.
               ===================================================== */

            BigDecimal tipoCambio =
                    obtenerTipoCambio(
                            xpath,
                            documento,
                            codigoMoneda
                    );


            String nombreArchivo =
                    obtenerNombreSeguro(
                            archivo.getOriginalFilename()
                    );


            /* =====================================================
               CONTROL DE CAMPOS ENCONTRADOS
               ===================================================== */

            int camposEncontrados =
                    contarCampos(

                            fechaEmision,

                            fechaVencimiento,

                            tipoComprobante,

                            comprobante.serie(),

                            comprobante.numero(),

                            moneda,

                            importeTotal,

                            datosTributarios.baseImponible(),

                            datosTributarios.igv(),

                            datosTributarios.inafecto(),

                            datosTributarios.isc(),

                            datosTributarios.icbper(),

                            datosTributarios.exonerado(),

                            datosTributarios.porcentajeIgv(),

                            tipoCambio,

                            documentoEmisor,

                            razonSocial,

                            descripcion
                    );


            return new ComprobanteXmlResponse(

                    nombreArchivo,

                    hashXml,

                    fechaEmision,

                    fechaVencimiento,

                    tipoComprobante,

                    codigoTipo,

                    comprobante.serie(),

                    comprobante.numero(),

                    moneda,

                    codigoMoneda,

                    importeTotal,

                    datosTributarios.baseImponible(),

                    datosTributarios.igv(),

                    datosTributarios.inafecto(),

                    datosTributarios.isc(),

                    datosTributarios.icbper(),

                    datosTributarios.exonerado(),

                    datosTributarios.porcentajeIgv(),

                    tipoCambio,

                    documentoEmisor,

                    razonSocial,

                    descripcion,

                    camposEncontrados
            );


        } catch (IllegalArgumentException ex) {

            throw ex;

        } catch (Exception ex) {

            throw new IllegalArgumentException(
                    "No se pudo procesar el archivo XML.",
                    ex
            );
        }
    }


    /* =========================================================
       OBTENER DATOS TRIBUTARIOS
       ========================================================= */

    private DatosTributarios obtenerDatosTributarios(
            XPath xpath,
            Document documento
    ) {

        BigDecimal baseImponible = CERO;
        BigDecimal igv = CERO;

        BigDecimal inafecto = CERO;
        BigDecimal isc = CERO;
        BigDecimal icbper = CERO;
        BigDecimal exonerado = CERO;

        BigDecimal porcentajeIgv = null;


        try {

            /*
             * Solo se analizan los TaxSubtotal que pertenecen
             * al TaxTotal principal de la factura.
             *
             * De esta manera evitamos sumar nuevamente los
             * impuestos que también aparecen a nivel de línea.
             */
            NodeList subtotales =
                    (NodeList)
                            xpath.evaluate(
                                    "/*[local-name()='Invoice']" +
                                            "/*[local-name()='TaxTotal']" +
                                            "/*[local-name()='TaxSubtotal']",
                                    documento,
                                    XPathConstants.NODESET
                            );


            for (
                    int i = 0;
                    i < subtotales.getLength();
                    i++
            ) {

                Node subtotal =
                        subtotales.item(i);


                String codigoTributo =
                        obtenerTextoNodo(
                                xpath,
                                subtotal,
                                "./*[local-name()='TaxCategory']" +
                                        "/*[local-name()='TaxScheme']" +
                                        "/*[local-name()='ID'][1]"
                        );


                if (codigoTributo == null) {
                    continue;
                }


                BigDecimal base =
                        convertirMonto(
                                obtenerTextoNodo(
                                        xpath,
                                        subtotal,
                                        "./*[local-name()='TaxableAmount'][1]"
                                )
                        );


                BigDecimal impuesto =
                        convertirMonto(
                                obtenerTextoNodo(
                                        xpath,
                                        subtotal,
                                        "./*[local-name()='TaxAmount'][1]"
                                )
                        );


                BigDecimal porcentaje =
                        convertirMonto(
                                obtenerTextoNodo(
                                        xpath,
                                        subtotal,
                                        "./*[local-name()='TaxCategory']" +
                                                "/*[local-name()='Percent'][1]"
                                )
                        );


                switch (codigoTributo) {

                    case CODIGO_IGV -> {

                        baseImponible =
                                sumar(
                                        baseImponible,
                                        base
                                );

                        igv =
                                sumar(
                                        igv,
                                        impuesto
                                );

                        /*
                         * Guardamos el primer porcentaje IGV
                         * encontrado.
                         *
                         * Si posteriormente aparecen facturas con
                         * diferentes tasas de IGV en un mismo XML,
                         * deberán clasificarse en BASE_IMP2/IGV2,
                         * BASE_IMP3/IGV3 según la regla contable
                         * que defina el reporte.
                         */
                        if (
                                porcentajeIgv == null
                                        &&
                                        porcentaje != null
                        ) {

                            porcentajeIgv =
                                    porcentaje;
                        }
                    }


                    case CODIGO_ISC ->

                            isc =
                                    sumar(
                                            isc,
                                            impuesto
                                    );


                    case CODIGO_ICBPER ->

                            icbper =
                                    sumar(
                                            icbper,
                                            impuesto
                                    );


                    case CODIGO_EXONERADO ->

                            exonerado =
                                    sumar(
                                            exonerado,
                                            base
                                    );


                    case CODIGO_INAFECTO ->

                            inafecto =
                                    sumar(
                                            inafecto,
                                            base
                                    );


                    default -> {
                        /*
                         * Otros tributos no forman parte
                         * actualmente del Registro de Compras
                         * solicitado.
                         */
                    }
                }
            }


        } catch (Exception ex) {

            throw new IllegalArgumentException(
                    "No se pudieron obtener los datos tributarios del XML.",
                    ex
            );
        }


        return new DatosTributarios(

                baseImponible,

                igv,

                inafecto,

                isc,

                icbper,

                exonerado,

                porcentajeIgv
        );
    }


    /* =========================================================
       FECHA DE VENCIMIENTO
       ========================================================= */

    private String obtenerFechaVencimiento(
            XPath xpath,
            Document documento
    ) {

        /*
         * Primera alternativa:
         * DueDate a nivel de Invoice.
         */
        String fecha =
                obtenerTexto(
                        xpath,
                        documento,
                        "/*[local-name()='Invoice']" +
                                "/*[local-name()='DueDate'][1]"
                );


        if (fecha != null) {
            return fecha;
        }


        /*
         * Segunda alternativa:
         * PaymentTerms/DueDate.
         */
        fecha =
                obtenerTexto(
                        xpath,
                        documento,
                        "/*[local-name()='Invoice']" +
                                "/*[local-name()='PaymentTerms']" +
                                "/*[local-name()='DueDate'][1]"
                );


        if (fecha != null) {
            return fecha;
        }


        /*
         * Algunas facturas a crédito informan la fecha
         * mediante PaymentDueDate.
         */
        return obtenerTexto(
                xpath,
                documento,
                "/*[local-name()='Invoice']" +
                        "/*[local-name()='PaymentMeans']" +
                        "/*[local-name()='PaymentDueDate'][1]"
        );
    }


    /* =========================================================
       TIPO DE CAMBIO
       ========================================================= */

    private BigDecimal obtenerTipoCambio(
            XPath xpath,
            Document documento,
            String codigoMoneda
    ) {

        if (
                codigoMoneda != null
                        &&
                        "PEN".equalsIgnoreCase(
                                codigoMoneda
                        )
        ) {

            return BigDecimal.ONE;
        }


        /*
         * Para moneda extranjera intentamos obtener el
         * CalculationRate informado por el XML.
         *
         * Si no existe, devolvemos NULL.
         */
        String valor =
                obtenerTexto(
                        xpath,
                        documento,
                        "/*[local-name()='Invoice']" +
                                "/*[local-name()='PaymentExchangeRate']" +
                                "/*[local-name()='CalculationRate'][1]"
                );


        if (valor == null) {

            valor =
                    obtenerTexto(
                            xpath,
                            documento,
                            "/*[local-name()='Invoice']" +
                                    "/*[local-name()='TaxExchangeRate']" +
                                    "/*[local-name()='CalculationRate'][1]"
                    );
        }


        return convertirMonto(
                valor
        );
    }


    /* =========================================================
       GLOSA
       ========================================================= */

    private String obtenerGlosa(
            XPath xpath,
            Document documento
    ) {

        try {

            NodeList descripciones =
                    (NodeList)
                            xpath.evaluate(
                                    "/*[local-name()='Invoice']" +
                                            "/*[local-name()='InvoiceLine']" +
                                            "/*[local-name()='Item']" +
                                            "/*[local-name()='Description']",
                                    documento,
                                    XPathConstants.NODESET
                            );


            Set<String> valoresUnicos =
                    new LinkedHashSet<>();


            for (
                    int i = 0;
                    i < descripciones.getLength();
                    i++
            ) {

                String valor =
                        descripciones
                                .item(i)
                                .getTextContent();


                if (valor == null) {
                    continue;
                }


                valor =
                        valor.trim();


                if (!valor.isBlank()) {

                    valoresUnicos.add(
                            valor
                    );
                }
            }


            if (valoresUnicos.isEmpty()) {

                return null;
            }


            List<String> valores =
                    new ArrayList<>(
                            valoresUnicos
                    );


            /*
             * Si existe un solo concepto se conserva tal cual.
             */
            if (valores.size() == 1) {

                return limitarTexto(
                        valores.get(0),
                        150
                );
            }


            /*
             * Si existen varios conceptos, se genera una glosa
             * compacta sin repetir descripciones.
             */
            String glosa =
                    String.join(
                            "; ",
                            valores
                    );


            return limitarTexto(
                    glosa,
                    150
            );


        } catch (Exception ex) {

            return null;
        }
    }


    /* =========================================================
       ARCHIVO
       ========================================================= */

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


    /* =========================================================
       LECTURA XML SEGURA
       ========================================================= */

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


    /* =========================================================
       XPATH
       ========================================================= */

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


        } catch (Exception ex) {

            return null;
        }
    }


    private String obtenerTextoNodo(
            XPath xpath,
            Node nodo,
            String expresion
    ) {

        try {

            String resultado =
                    (String)
                            xpath.evaluate(
                                    expresion,
                                    nodo,
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


        } catch (Exception ex) {

            return null;
        }
    }


    /* =========================================================
       CONVERSIONES
       ========================================================= */

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

        } catch (Exception ex) {

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

        } catch (NumberFormatException ex) {

            return null;
        }
    }


    private BigDecimal sumar(
            BigDecimal actual,
            BigDecimal valor
    ) {

        if (actual == null) {
            actual = CERO;
        }


        if (valor == null) {
            return actual;
        }


        return actual.add(
                valor
        );
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


    /* =========================================================
       COMPROBANTE
       ========================================================= */

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


    /* =========================================================
       HASH
       ========================================================= */

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


        } catch (NoSuchAlgorithmException ex) {

            throw new IllegalStateException(
                    "No se pudo calcular el hash del XML.",
                    ex
            );
        }
    }


    /* =========================================================
       UTILIDADES
       ========================================================= */

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


    private String limitarTexto(
            String valor,
            int longitudMaxima
    ) {

        if (valor == null) {
            return null;
        }


        String resultado =
                valor.trim();


        if (resultado.isBlank()) {
            return null;
        }


        if (
                resultado.length()
                        <=
                        longitudMaxima
        ) {

            return resultado;
        }


        return resultado
                .substring(
                        0,
                        longitudMaxima
                )
                .trim();
    }


    private int contarCampos(
            Object... valores
    ) {

        int total = 0;


        for (
                Object valor : valores
        ) {

            if (valor != null) {

                total++;
            }
        }


        return total;
    }


    /* =========================================================
       RECORDS INTERNOS
       ========================================================= */

    private record DocumentoNumero(
            String serie,
            String numero
    ) {
    }


    private record DatosTributarios(

            BigDecimal baseImponible,

            BigDecimal igv,

            BigDecimal inafecto,

            BigDecimal isc,

            BigDecimal icbper,

            BigDecimal exonerado,

            BigDecimal porcentajeIgv

    ) {
    }
}