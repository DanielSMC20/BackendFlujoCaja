package com.flujocaja.flujo_caja_api.compartido.excepcion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> manejarCredenciales(
            BadCredentialsException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ApiError(
                                Instant.now(),
                                HttpStatus.UNAUTHORIZED.value(),
                                ex.getMessage()
                        )
                );
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manejarValidacion(
            MethodArgumentNotValidException ex
    ) {

        String mensaje =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .findFirst()
                        .map(error ->
                                error.getField()
                                        + ": "
                                        + error.getDefaultMessage()
                        )
                        .orElse(
                                "La solicitud contiene datos inválidos."
                        );


        return ResponseEntity
                .badRequest()
                .body(
                        new ApiError(
                                Instant.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                mensaje
                        )
                );
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> manejarArgumento(
            IllegalArgumentException ex
    ) {

        return ResponseEntity
                .badRequest()
                .body(
                        new ApiError(
                                Instant.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                ex.getMessage()
                        )
                );
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> manejarEstado(
            IllegalStateException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ApiError(
                                Instant.now(),
                                HttpStatus.CONFLICT.value(),
                                ex.getMessage()
                        )
                );
    }
}