package com.flujocaja.flujo_caja_api.cargamasiva.servicio;

import com.flujocaja.flujo_caja_api.categoria.dto.CategoriaResponse;
import com.flujocaja.flujo_caja_api.categoria.servicio.CategoriaService;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

@Service
public class PlantillaCargaMasivaService {

    private static final int TIPO_EGRESO = 2;

    private static final int PRIMERA_FILA_DATOS = 1;
    private static final int ULTIMA_FILA_DATOS = 300;

    private static final int COLUMNA_FECHA = 0;
    private static final int COLUMNA_DESCRIPCION = 1;
    private static final int COLUMNA_MONTO = 2;
    private static final int COLUMNA_CATEGORIA = 3;
    private static final int COLUMNA_ESTADO = 4;

    private static final int COLUMNA_NOMBRE_CATEGORIA = 0;

    private static final String HOJA_CARGA = "Carga";
    private static final String HOJA_CLASIFICADORES = "Clasificadores";
    private static final String HOJA_EJEMPLO = "Ejemplo";

    private static final String NOMBRE_RANGO_CATEGORIAS =
            "CategoriasActivas";

    private static final ZoneId ZONA_HORARIA =
            ZoneId.of("America/Lima");

    private static final String RUTA_PLANTILLA =
            "plantillas/plantilla-carga-masiva-egresos.xlsx";

    private final CategoriaService categoriaService;


    public PlantillaCargaMasivaService(
            CategoriaService categoriaService
    ) {

        this.categoriaService = categoriaService;
    }


    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','CONTADOR','OPERADOR')"
    )
    public byte[] generar() {

        List<String> categorias =
                categoriaService
                        .listar(
                                TIPO_EGRESO,
                                true
                        )
                        .stream()
                        .filter(
                                categoria ->
                                        Boolean.TRUE.equals(
                                                categoria.activo()
                                        )
                        )
                        .map(
                                CategoriaResponse::nombre
                        )
                        .filter(
                                nombre ->
                                        nombre != null
                                                &&
                                                !nombre.isBlank()
                        )
                        .map(String::trim)
                        .distinct()
                        .sorted(
                                String.CASE_INSENSITIVE_ORDER
                        )
                        .toList();


        if (categorias.isEmpty()) {

            throw new IllegalStateException(
                    "La empresa debe registrar al menos una categoría activa de egreso antes de descargar la plantilla."
            );
        }


        Resource recurso =
                new ClassPathResource(
                        RUTA_PLANTILLA
                );


        try (
                InputStream entrada =
                        recurso.getInputStream();

                XSSFWorkbook libro =
                        new XSSFWorkbook(
                                entrada
                        );

                ByteArrayOutputStream salida =
                        new ByteArrayOutputStream()
        ) {

            XSSFSheet hojaCarga =
                    obtenerHoja(
                            libro,
                            HOJA_CARGA
                    );


            XSSFSheet hojaClasificadores =
                    obtenerHoja(
                            libro,
                            HOJA_CLASIFICADORES
                    );


            XSSFSheet hojaEjemplo =
                    obtenerHoja(
                            libro,
                            HOJA_EJEMPLO
                    );


            escribirCategorias(
                    hojaClasificadores,
                    categorias
            );


            escribirEjemplos(
                    hojaEjemplo,
                    categorias
            );


            configurarRangoCategorias(
                    libro,
                    categorias.size()
            );


            configurarValidaciones(
                    hojaCarga
            );


            libro.write(
                    salida
            );


            return salida.toByteArray();

        } catch (IOException ex) {

            throw new IllegalStateException(
                    "No fue posible generar la plantilla de carga masiva.",
                    ex
            );
        }
    }


    private XSSFSheet obtenerHoja(
            XSSFWorkbook libro,
            String nombre
    ) {

        XSSFSheet hoja =
                libro.getSheet(
                        nombre
                );


        if (hoja == null) {

            throw new IllegalStateException(
                    "La plantilla base no contiene la hoja \""
                            +
                            nombre
                            +
                            "\"."
            );
        }


        return hoja;
    }


    private void escribirCategorias(
            XSSFSheet hoja,
            List<String> categorias
    ) {

        CellStyle estiloBase =
                obtenerEstiloBaseCategoria(
                        hoja
                );


        int ultimaFilaExistente =
                Math.max(
                        hoja.getLastRowNum(),
                        PRIMERA_FILA_DATOS
                                +
                                categorias.size()
                );


        for (
                int indiceFila = PRIMERA_FILA_DATOS;
                indiceFila <= ultimaFilaExistente;
                indiceFila++
        ) {

            Row fila =
                    hoja.getRow(
                            indiceFila
                    );


            if (fila == null) {

                continue;
            }


            Cell celda =
                    fila.getCell(
                            COLUMNA_NOMBRE_CATEGORIA
                    );


            if (celda != null) {

                celda.setBlank();
            }
        }


        for (
                int indice = 0;
                indice < categorias.size();
                indice++
        ) {

            int indiceFila =
                    PRIMERA_FILA_DATOS
                            +
                            indice;


            Row fila =
                    hoja.getRow(
                            indiceFila
                    );


            if (fila == null) {

                fila =
                        hoja.createRow(
                                indiceFila
                        );
            }


            Cell celda =
                    fila.getCell(
                            COLUMNA_NOMBRE_CATEGORIA
                    );


            if (celda == null) {

                celda =
                        fila.createCell(
                                COLUMNA_NOMBRE_CATEGORIA
                        );
            }


            if (estiloBase != null) {

                celda.setCellStyle(
                        estiloBase
                );
            }


            celda.setCellValue(
                    categorias.get(
                            indice
                    )
            );
        }
    }


    private CellStyle obtenerEstiloBaseCategoria(
            XSSFSheet hoja
    ) {

        Row fila =
                hoja.getRow(
                        PRIMERA_FILA_DATOS
                );


        if (fila == null) {

            return null;
        }


        Cell celda =
                fila.getCell(
                        COLUMNA_NOMBRE_CATEGORIA
                );


        return celda == null
                ? null
                : celda.getCellStyle();
    }


    private void escribirEjemplos(
            XSSFSheet hoja,
            List<String> categorias
    ) {

        LocalDate fechaActual =
                LocalDate.now(
                        ZONA_HORARIA
                );


        for (
                int indice = 0;
                indice < 5;
                indice++
        ) {

            int indiceFila =
                    PRIMERA_FILA_DATOS
                            +
                            indice;


            Row fila =
                    hoja.getRow(
                            indiceFila
                    );


            if (fila == null) {

                fila =
                        hoja.createRow(
                                indiceFila
                        );
            }


            String categoria =
                    categorias.get(
                            indice
                                    %
                                    categorias.size()
                    );


            boolean pagado =
                    indice < 2;


            LocalDate fecha =
                    pagado

                            ? fechaActual

                            : fechaActual.plusDays(
                            indice + 1L
                    );


            obtenerOCrearCelda(
                    fila,
                    COLUMNA_FECHA
            ).setCellValue(
                    Date.valueOf(
                            fecha
                    )
            );


            obtenerOCrearCelda(
                    fila,
                    COLUMNA_DESCRIPCION
            ).setCellValue(
                    "EJEMPLO - "
                            +
                            categoria.toUpperCase(
                                    Locale.ROOT
                            )
            );


            obtenerOCrearCelda(
                    fila,
                    COLUMNA_MONTO
            ).setCellValue(
                    100.00
                            +
                            indice * 50.00
            );


            obtenerOCrearCelda(
                    fila,
                    COLUMNA_CATEGORIA
            ).setCellValue(
                    categoria
            );


            obtenerOCrearCelda(
                    fila,
                    COLUMNA_ESTADO
            ).setCellValue(
                    pagado
                            ? "SÍ"
                            : "NO"
            );
        }
    }


    private Cell obtenerOCrearCelda(
            Row fila,
            int columna
    ) {

        Cell celda =
                fila.getCell(
                        columna
                );


        return celda != null

                ? celda

                : fila.createCell(
                columna
        );
    }


    private void configurarRangoCategorias(
            XSSFWorkbook libro,
            int cantidadCategorias
    ) {

        Name rango =
                libro.getName(
                        NOMBRE_RANGO_CATEGORIAS
                );


        if (rango == null) {

            rango =
                    libro.createName();

            rango.setNameName(
                    NOMBRE_RANGO_CATEGORIAS
            );
        }


        int ultimaFilaExcel =
                cantidadCategorias
                        +
                        1;


        rango.setRefersToFormula(
                "'"
                        +
                        HOJA_CLASIFICADORES
                        +
                        "'!$A$2:$A$"
                        +
                        ultimaFilaExcel
        );
    }


    private void configurarValidaciones(
            XSSFSheet hoja
    ) {

        /*
         * Eliminamos las validaciones que pueda traer
         * la plantilla base para volver a generarlas.
         */
        if (
                hoja.getCTWorksheet()
                        .isSetDataValidations()
        ) {

            hoja.getCTWorksheet()
                    .unsetDataValidations();
        }


        DataValidationHelper ayuda =
                hoja.getDataValidationHelper();


        /*
         * FECHA
         *
         * Mantiene validación estricta.
         */
        agregarValidacion(
                hoja,
                ayuda.createDateConstraint(
                        DataValidationConstraint.OperatorType.BETWEEN,
                        "DATE(2000,1,1)",
                        "DATE(2100,12,31)",
                        "dd/mm/yyyy"
                ),
                COLUMNA_FECHA,
                "Fecha no válida",
                "Ingresa una fecha válida con el formato día/mes/año.",
                false
        );


        /*
         * DESCRIPCIÓN
         *
         * De 1 a 150 caracteres.
         */
        agregarValidacion(
                hoja,
                ayuda.createTextLengthConstraint(
                        DataValidationConstraint.OperatorType.BETWEEN,
                        "1",
                        "150"
                ),
                COLUMNA_DESCRIPCION,
                "Descripción no válida",
                "La descripción debe contener entre 1 y 150 caracteres."
        );


        /*
         * MONTO
         *
         * Debe ser mayor o igual a 0.01.
         */
        agregarValidacion(
                hoja,
                ayuda.createDecimalConstraint(
                        DataValidationConstraint.OperatorType.GREATER_OR_EQUAL,
                        "0.01",
                        null
                ),
                COLUMNA_MONTO,
                "Monto no válido",
                "El monto debe ser numérico y mayor que cero."
        );


        /*
         * CATEGORÍA
         *
         * Solo permite categorías activas.
         */
        agregarValidacion(
                hoja,
                ayuda.createFormulaListConstraint(
                        NOMBRE_RANGO_CATEGORIAS
                ),
                COLUMNA_CATEGORIA,
                "Clasificador no válido",
                "Selecciona un clasificador activo de la lista.",
                false
        );


        /*
         * ¿YA SE PAGÓ?
         *
         * Sigue mostrando el desplegable NO / SÍ.
         *
         * FALSE:
         * No mostramos el bloqueo de Excel para esta
         * columna, facilitando copiar y pegar valores.
         */
        agregarValidacion(
                hoja,
                ayuda.createExplicitListConstraint(
                        new String[]{
                                "NO",
                                "SÍ"
                        }
                ),
                COLUMNA_ESTADO,
                "Estado no válido",
                "Selecciona SÍ para pagado o NO para proyectado.",
                false
        );
    }


    /**
     * Método por defecto.
     *
     * Todas las validaciones que usen esta versión
     * bloquearán valores incorrectos.
     */
    private void agregarValidacion(
            XSSFSheet hoja,
            DataValidationConstraint restriccion,
            int columna,
            String tituloError,
            String mensajeError
    ) {

        agregarValidacion(
                hoja,
                restriccion,
                columna,
                tituloError,
                mensajeError,
                true
        );
    }


    /**
     * Método configurable.
     *
     * @param bloquearValorInvalido
     * true  = Excel muestra error y bloquea un valor no válido.
     * false = no muestra el error de bloqueo.
     */
    private void agregarValidacion(
            XSSFSheet hoja,
            DataValidationConstraint restriccion,
            int columna,
            String tituloError,
            String mensajeError,
            boolean bloquearValorInvalido
    ) {

        CellRangeAddressList rango =
                new CellRangeAddressList(
                        PRIMERA_FILA_DATOS,
                        ULTIMA_FILA_DATOS,
                        columna,
                        columna
                );


        DataValidation validacion =
                hoja.getDataValidationHelper()
                        .createValidation(
                                restriccion,
                                rango
                        );


        validacion.setEmptyCellAllowed(true);
        validacion.setShowErrorBox(
                bloquearValorInvalido
        );


        if (bloquearValorInvalido) {

            validacion.createErrorBox(
                    tituloError,
                    mensajeError
            );
        }


        hoja.addValidationData(
                validacion
        );
    }
}