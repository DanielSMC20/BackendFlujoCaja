package com.flujocaja.flujo_caja_api.comun.excepcion;

import com.flujocaja.flujo_caja_api.comun.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.flujocaja.flujo_caja_api.comun.excepcion.RecursoNoEncontradoException;

import java.sql.SQLException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    GlobalExceptionHandler.class
            );


    /*
     * ===============================================
     * VALIDACIONES @Valid
     * ===============================================
     */
    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ApiErrorResponse> manejarValidacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, String> campos =
                new LinkedHashMap<>();


        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->

                        campos.putIfAbsent(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );


        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Existen datos inválidos.",
                request.getRequestURI(),
                campos
        );
    }


    /*
     * ===============================================
     * PARÁMETROS CON TIPO INCORRECTO
     * ===============================================
     */
    @ExceptionHandler(
            MethodArgumentTypeMismatchException.class
    )
    public ResponseEntity<ApiErrorResponse> manejarTipoParametro(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "El parámetro '" +
                        ex.getName() +
                        "' tiene un valor inválido.",
                request.getRequestURI(),
                Map.of()
        );
    }


    /*
     * ===============================================
     * VALIDACIONES DEL SERVICE
     * ===============================================
     */
    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ResponseEntity<ApiErrorResponse> manejarArgumentoInvalido(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI(),
                Map.of()
        );
    }


    /*
     * ===============================================
     * LOGIN INCORRECTO
     * ===============================================
     */
    @ExceptionHandler(
            BadCredentialsException.class
    )
    public ResponseEntity<ApiErrorResponse> manejarCredenciales(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {

        return construirRespuesta(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI(),
                Map.of()
        );
    }


    /*
     * ===============================================
     * SIN PERMISOS
     * ===============================================
     */
    @ExceptionHandler(
            AccessDeniedException.class
    )
    public ResponseEntity<ApiErrorResponse> manejarAccesoDenegado(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {

        return construirRespuesta(
                HttpStatus.FORBIDDEN,
                "No tiene permisos para realizar esta operación.",
                request.getRequestURI(),
                Map.of()
        );
    }


    /*
     * ===============================================
     * ERRORES DE PROCEDIMIENTOS ALMACENADOS
     * ===============================================
     */
    @ExceptionHandler(
            DataAccessException.class
    )
    public ResponseEntity<ApiErrorResponse> manejarBaseDatos(
            DataAccessException ex,
            HttpServletRequest request
    ) {

        SQLException sqlException =
                buscarSQLException(ex);


        /*
         * Los errores que nosotros generamos
         * mediante THROW empiezan desde 50000.
         */
        if (
                sqlException != null
                        &&
                        sqlException.getErrorCode() >= 50000
        ) {

            return construirRespuesta(
                    HttpStatus.BAD_REQUEST,
                    limpiarMensaje(
                            sqlException.getMessage()
                    ),
                    request.getRequestURI(),
                    Map.of()
            );
        }


        /*
         * Cualquier otro error SQL no se expone
         * directamente al frontend.
         */
        logger.error(
                "Error de base de datos",
                ex
        );


        return construirRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error al procesar la operación.",
                request.getRequestURI(),
                Map.of()
        );
    }


    /*
     * ===============================================
     * ERROR NO CONTROLADO
     * ===============================================
     */
    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ApiErrorResponse> manejarErrorGeneral(
            Exception ex,
            HttpServletRequest request
    ) {

        logger.error(
                "Error no controlado",
                ex
        );


        return construirRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno.",
                request.getRequestURI(),
                Map.of()
        );
    }


    private SQLException buscarSQLException(
            Throwable throwable
    ) {

        Throwable actual =
                throwable;


        while (actual != null) {

            if (
                    actual instanceof SQLException sqlException
            ) {

                return sqlException;
            }


            actual =
                    actual.getCause();
        }


        return null;
    }


    private String limpiarMensaje(
            String mensaje
    ) {

        if (
                mensaje == null
                        ||
                        mensaje.isBlank()
        ) {

            return "La operación no pudo ser procesada.";
        }


        int saltoLinea =
                mensaje.indexOf('\n');


        if (saltoLinea > 0) {

            return mensaje
                    .substring(
                            0,
                            saltoLinea
                    )
                    .trim();
        }


        return mensaje.trim();
    }


    private ResponseEntity<ApiErrorResponse> construirRespuesta(
            HttpStatus status,
            String mensaje,
            String path,
            Map<String, String> campos
    ) {

        ApiErrorResponse response =
                new ApiErrorResponse(

                        Instant.now(),

                        status.value(),

                        status.getReasonPhrase(),

                        mensaje,

                        path,

                        campos
                );


        return ResponseEntity
                .status(status)
                .body(response);
    }
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> manejarNoEncontrado(
            RecursoNoEncontradoException ex,
            HttpServletRequest request
    ) {

        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI(),
                Map.of()
        );
    }
}