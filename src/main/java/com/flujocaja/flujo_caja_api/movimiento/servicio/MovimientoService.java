package com.flujocaja.flujo_caja_api.movimiento.servicio;

import com.flujocaja.flujo_caja_api.comprobante.repositorio.ComprobanteRepository;
import com.flujocaja.flujo_caja_api.comun.excepcion.RecursoNoEncontradoException;
import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoActualizarRequest;
import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoAnularRequest;
import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoPagoRequest;
import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoCrearRequest;
import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoDetalleResponse;
import com.flujocaja.flujo_caja_api.movimiento.dto.MovimientoResponse;
import com.flujocaja.flujo_caja_api.movimiento.repositorio.MovimientoRepository;
import com.flujocaja.flujo_caja_api.seguridad.servicio.ContextoSeguridad;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service

public class MovimientoService {

    /* =========================================================
       CONSTANTES DE NEGOCIO
       ========================================================= */

    private static final int TIPO_INGRESO = 1;
    private static final int TIPO_EGRESO = 2;

    private static final int ORIGEN_MANUAL = 1;
    private static final int ORIGEN_XML = 2;

    private static final int SIN_COMPROBANTE = 5;


    /* =========================================================
       DEPENDENCIAS
       ========================================================= */

    private final MovimientoRepository movimientoRepository;

    private final ComprobanteRepository comprobanteRepository;

    private final ContextoSeguridad contextoSeguridad;


    public MovimientoService(
            MovimientoRepository movimientoRepository,
            ComprobanteRepository comprobanteRepository,
            ContextoSeguridad contextoSeguridad
    ) {

        this.movimientoRepository =
                movimientoRepository;

        this.comprobanteRepository =
                comprobanteRepository;

        this.contextoSeguridad =
                contextoSeguridad;
    }


    /* =========================================================
       LISTAR
       ========================================================= */
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR', 'CONTADOR', 'OPERADOR', 'CONSULTA')"
    )
    public List<MovimientoResponse> listar(
            Integer tipoMovimiento,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Boolean cancelado,
            Boolean soloActivos
    ) {

        validarRangoFechas(
                fechaDesde,
                fechaHasta
        );


        /*
         * bCancelado solamente tiene sentido
         * para egresos.
         */
        if (
                Integer.valueOf(TIPO_INGRESO)
                        .equals(tipoMovimiento)
                        &&
                        cancelado != null
        ) {

            throw new IllegalArgumentException(
                    "El estado de pago solamente aplica a los egresos."
            );
        }


        return movimientoRepository.listar(

                contextoSeguridad.empresaId(),

                tipoMovimiento,

                fechaDesde,

                fechaHasta,

                cancelado,

                soloActivos == null
                        ? Boolean.TRUE
                        : soloActivos
        );
    }


    /* =========================================================
       OBTENER POR ID
       ========================================================= */

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR', 'CONTADOR', 'OPERADOR', 'CONSULTA')"
    )
    public MovimientoDetalleResponse obtenerPorId(
            Long movimientoId
    ) {

        return movimientoRepository
                .obtenerDetallePorId(

                        contextoSeguridad.empresaId(),

                        movimientoId
                )
                .orElseThrow(

                        () ->
                                new RecursoNoEncontradoException(
                                        "El movimiento no existe."
                                )
                );
    }


    /* =========================================================
       REGISTRAR
       ========================================================= */

    @Transactional
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','CONTADOR','OPERADOR')"
    )
    public MovimientoResponse registrar(
            MovimientoCrearRequest request
    ) {

        Integer empresaId =
                contextoSeguridad.empresaId();

        Long usuarioId =
                contextoSeguridad.usuarioId();


        /* -----------------------------------------------------
           COMPROBANTE
           ----------------------------------------------------- */

        validarComprobante(

                request.tipoMovimiento(),

                request.tipoComprobante(),

                request.numeroComprobante(),

                request.archivoXmlNombre(),

                request.hashXml()
        );


        /* -----------------------------------------------------
           ORIGEN
           ----------------------------------------------------- */

        boolean tieneXml =
                tieneXml(

                        request.archivoXmlNombre(),

                        request.hashXml()
                );


        int origenRegistro =
                tieneXml
                        ? ORIGEN_XML
                        : ORIGEN_MANUAL;


        /* -----------------------------------------------------
           ESTADO Y FECHAS
           ----------------------------------------------------- */

        EstadoRegistro estado =
                prepararEstadoRegistro(
                        request
                );


        /* -----------------------------------------------------
           MOVIMIENTO
           ----------------------------------------------------- */

        MovimientoResponse movimiento =
                movimientoRepository.registrar(
                        empresaId,
                        request.tipoMovimiento(),
                        request.categoriaId(),

                        estado.fechaMovimiento(),
                        estado.cancelado(),

                        request.descripcion().trim(),
                        request.monto(),
                        request.medioPago(),
                        request.tipoComprobante(),
                        request.moneda(),
                        origenRegistro,
                        request.observacion(),
                        usuarioId
                );




        comprobanteRepository.guardar(

                empresaId,

                movimiento.id(),

                request.fechaComprobante(),

                request.serieComprobante(),

                request.numeroComprobante(),

                request.documentoEmisor(),

                request.razonSocialEmisor(),

                request.archivoXmlNombre(),

                request.hashXml(),
                usuarioId

        );


        return movimiento;
    }


    /* =========================================================
       ACTUALIZAR
       ========================================================= */

    @Transactional
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','CONTADOR','OPERADOR')"
    )
    public MovimientoResponse actualizar(
            Long movimientoId,
            MovimientoActualizarRequest request
    ) {

        Integer empresaId =
                contextoSeguridad.empresaId();

        Long usuarioId =
                contextoSeguridad.usuarioId();


        /*
         * Primero obtenemos el movimiento para conocer
         * su tipo y estado actual.
         */
        MovimientoDetalleResponse existente =
                movimientoRepository
                        .obtenerDetallePorId(
                                empresaId,
                                movimientoId
                        )
                        .orElseThrow(

                                () ->
                                        new RecursoNoEncontradoException(
                                                "El movimiento no existe."
                                        )
                        );


        /*
         * Movimiento anulado.
         */
        if (
                Boolean.FALSE.equals(
                        existente.activo()
                )
        ) {

            throw new IllegalArgumentException(
                    "Un movimiento anulado no puede ser modificado."
            );
        }


        /*
         * Un egreso ya pagado no puede modificarse.
         *
         * La anulación es otra operación diferente.
         */
        if (
                Integer.valueOf(TIPO_EGRESO)
                        .equals(
                                existente.tipoMovimiento()
                        )
                        &&
                        Boolean.TRUE.equals(
                                existente.cancelado()
                        )
        ) {

            throw new IllegalArgumentException(
                    "Un egreso pagado no puede ser modificado."
            );
        }


        /* -----------------------------------------------------
           COMPROBANTE
           ----------------------------------------------------- */

        validarComprobante(

                existente.tipoMovimiento(),

                request.tipoComprobante(),

                request.numeroComprobante(),

                request.archivoXmlNombre(),

                request.hashXml()
        );


        /* -----------------------------------------------------
           FECHAS
           ----------------------------------------------------- */

        EstadoActualizacion estado =
                prepararEstadoActualizacion(

                        existente,

                        request
                );


        /* -----------------------------------------------------
           MOVIMIENTO
           ----------------------------------------------------- */

        MovimientoResponse movimiento =
                movimientoRepository.actualizar(

                        movimientoId,

                        empresaId,

                        existente.tipoMovimiento(),

                        request.categoriaId(),

                        estado.fechaMovimiento(),

                        existente.cancelado(),

                        request.descripcion().trim(),

                        request.monto(),

                        request.medioPago(),

                        request.tipoComprobante(),

                        request.moneda(),

                        existente.origenRegistro(),

                        request.observacion(),

                        usuarioId
                );


        /* -----------------------------------------------------
           COMPROBANTE
           ----------------------------------------------------- */

        comprobanteRepository.guardar(

                empresaId,

                movimientoId,

                request.fechaComprobante(),

                request.serieComprobante(),

                request.numeroComprobante(),

                request.documentoEmisor(),

                request.razonSocialEmisor(),

                request.archivoXmlNombre(),

                request.hashXml(),
                usuarioId

        );


        return movimiento;
    }


    /* =========================================================
       MARCAR EGRESO COMO PAGADO
       ========================================================= */

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','CONTADOR','OPERADOR')"
    )
    public MovimientoDetalleResponse marcarComoPagado(
            Long movimientoId,
            MovimientoPagoRequest request
    ) {
        if (movimientoId == null || movimientoId <= 0) {
            throw new IllegalArgumentException(
                    "El movimiento es obligatorio."
            );
        }

        if (request == null || request.fechaPago() == null) {
            throw new IllegalArgumentException(
                    "La fecha de pago es obligatoria."
            );
        }

        movimientoRepository.marcarComoPagado(
                contextoSeguridad.empresaId(),
                movimientoId,
                request.fechaPago(),
                contextoSeguridad.usuarioId()
        );

        return obtenerPorId(movimientoId);
    }


    /* =========================================================
       ANULAR
       ========================================================= */

    @Transactional
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','CONTADOR')"
    )
    public void anular(

            Long movimientoId,

            MovimientoAnularRequest request
    ) {

        if (
                movimientoId == null
                        ||
                        movimientoId <= 0
        ) {

            throw new IllegalArgumentException(
                    "El movimiento es obligatorio."
            );
        }


        if (
                request == null
                        ||
                        request.motivo() == null
                        ||
                        request.motivo().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "El motivo de anulación es obligatorio."
            );
        }


        movimientoRepository.anular(

                contextoSeguridad.empresaId(),

                movimientoId,

                request.motivo().trim(),

                contextoSeguridad.usuarioId()
        );
    }


    /* =========================================================
       PREPARAR ESTADO AL REGISTRAR
       ========================================================= */

    private EstadoRegistro prepararEstadoRegistro(
            MovimientoCrearRequest request
    ) {

        Integer tipo =
                request.tipoMovimiento();


        if (tipo == null) {

            throw new IllegalArgumentException(
                    "El tipo de movimiento es obligatorio."
            );
        }


        /* =====================================================
           INGRESO
           ===================================================== */

        if (
                tipo == TIPO_INGRESO
        ) {

            if (
                    request.fechaMovimiento() == null
            ) {

                throw new IllegalArgumentException(
                        "La fecha del ingreso es obligatoria."
                );
            }


            if (
                    request.cancelado() != null
            ) {

                throw new IllegalArgumentException(
                        "Un ingreso no utiliza el estado de pago."
                );
            }


            if (
                    request.fechaProyectada() != null
                            ||
                            request.fechaPago() != null
            ) {

                throw new IllegalArgumentException(
                        "Un ingreso no utiliza fecha proyectada ni fecha de pago."
                );
            }


            return new EstadoRegistro(

                    request.fechaMovimiento(),

                    null,

                    null,

                    null
            );
        }


        /* =====================================================
           EGRESO
           ===================================================== */

        if (
                tipo == TIPO_EGRESO
        ) {

            if (
                    request.cancelado() == null
            ) {

                throw new IllegalArgumentException(
                        "Debe indicar si el egreso está pagado o proyectado."
                );
            }


            /* -------------------------------------------------
               PROYECTADO
               ------------------------------------------------- */

            if (
                    Boolean.FALSE.equals(
                            request.cancelado()
                    )
            ) {

                LocalDate fechaProyectada =
                        request.fechaProyectada() != null

                                ? request.fechaProyectada()

                                : request.fechaMovimiento();


                if (
                        fechaProyectada == null
                ) {

                    throw new IllegalArgumentException(
                            "La fecha proyectada del egreso es obligatoria."
                    );
                }


                if (
                        request.fechaPago() != null
                ) {

                    throw new IllegalArgumentException(
                            "Un egreso proyectado no puede tener fecha de pago."
                    );
                }


                if (
                        request.fechaMovimiento() != null
                                &&
                                request.fechaProyectada() != null
                                &&
                                !request.fechaMovimiento()
                                        .equals(
                                                request.fechaProyectada()
                                        )
                ) {

                    throw new IllegalArgumentException(
                            "La fecha del movimiento debe coincidir con la fecha proyectada."
                    );
                }


                return new EstadoRegistro(

                        fechaProyectada,

                        fechaProyectada,

                        null,

                        false
                );
            }


            /* -------------------------------------------------
               PAGADO
               ------------------------------------------------- */

            LocalDate fechaPago =
                    request.fechaPago() != null

                            ? request.fechaPago()

                            : request.fechaMovimiento();


            if (
                    fechaPago == null
            ) {

                throw new IllegalArgumentException(
                        "La fecha de pago del egreso es obligatoria."
                );
            }


            if (
                    request.fechaMovimiento() != null
                            &&
                            request.fechaPago() != null
                            &&
                            !request.fechaMovimiento()
                                    .equals(
                                            request.fechaPago()
                                    )
            ) {

                throw new IllegalArgumentException(
                        "La fecha del movimiento debe coincidir con la fecha de pago."
                );
            }


            return new EstadoRegistro(

                    fechaPago,

                    request.fechaProyectada(),

                    fechaPago,

                    true
            );
        }


        throw new IllegalArgumentException(
                "El tipo de movimiento no es válido."
        );
    }


    /* =========================================================
       PREPARAR ESTADO AL ACTUALIZAR
       ========================================================= */

    private EstadoActualizacion prepararEstadoActualizacion(
            MovimientoDetalleResponse existente,
            MovimientoActualizarRequest request
    ) {

        /* =====================================================
           INGRESO
           ===================================================== */

        if (
                existente.tipoMovimiento()
                        ==
                        TIPO_INGRESO
        ) {

            if (
                    request.fechaMovimiento() == null
            ) {

                throw new IllegalArgumentException(
                        "La fecha del ingreso es obligatoria."
                );
            }


            if (
                    request.fechaProyectada() != null
            ) {

                throw new IllegalArgumentException(
                        "Un ingreso no utiliza fecha proyectada."
                );
            }


            return new EstadoActualizacion(

                    request.fechaMovimiento(),

                    null
            );
        }


        /* =====================================================
           EGRESO PROYECTADO
           ===================================================== */

        if (
                existente.tipoMovimiento()
                        ==
                        TIPO_EGRESO
        ) {

            LocalDate fechaProyectada =
                    request.fechaProyectada() != null

                            ? request.fechaProyectada()

                            : request.fechaMovimiento();


            if (
                    fechaProyectada == null
            ) {

                throw new IllegalArgumentException(
                        "La fecha proyectada del egreso es obligatoria."
                );
            }


            if (
                    request.fechaMovimiento() != null
                            &&
                            request.fechaProyectada() != null
                            &&
                            !request.fechaMovimiento()
                                    .equals(
                                            request.fechaProyectada()
                                    )
            ) {

                throw new IllegalArgumentException(
                        "La fecha del movimiento debe coincidir con la fecha proyectada."
                );
            }


            return new EstadoActualizacion(

                    fechaProyectada,

                    fechaProyectada
            );
        }


        throw new IllegalArgumentException(
                "El tipo de movimiento no es válido."
        );
    }


    /* =========================================================
       VALIDAR COMPROBANTE
       ========================================================= */

    private void validarComprobante(
            Integer tipoMovimiento,
            Integer tipoComprobante,
            String numeroComprobante,
            String archivoXmlNombre,
            String hashXml
    ) {

        boolean tieneNombreXml =
                archivoXmlNombre != null
                        &&
                        !archivoXmlNombre.isBlank();


        boolean tieneHashXml =
                hashXml != null
                        &&
                        !hashXml.isBlank();


        /*
         * El XML siempre debe tener:
         *
         * nombre + hash
         */
        if (
                tieneNombreXml
                        !=
                        tieneHashXml
        ) {

            throw new IllegalArgumentException(
                    "La información del XML está incompleta."
            );
        }


        boolean xml =
                tieneNombreXml
                        &&
                        tieneHashXml;


        /*
         * XML solamente para egresos.
         */
        if (
                xml
                        &&
                        !Integer.valueOf(TIPO_EGRESO)
                                .equals(tipoMovimiento)
        ) {

            throw new IllegalArgumentException(
                    "El XML solamente puede utilizarse en un egreso."
            );
        }


        /*
         * Si tiene XML, necesariamente debe existir
         * un tipo de comprobante.
         */
        if (
                xml
                        &&
                        (
                                tipoComprobante == null
                                        ||
                                        tipoComprobante == SIN_COMPROBANTE
                        )
        ) {

            throw new IllegalArgumentException(
                    "Un registro mediante XML debe tener un tipo de comprobante."
            );
        }


        /*
         * Para un comprobante real,
         * el número es obligatorio.
         */
        if (
                tipoComprobante != null
                        &&
                        tipoComprobante != SIN_COMPROBANTE
                        &&
                        (
                                numeroComprobante == null
                                        ||
                                        numeroComprobante.isBlank()
                        )
        ) {

            throw new IllegalArgumentException(
                    "El número de comprobante es obligatorio."
            );
        }
    }


    /* =========================================================
       EXISTE XML
       ========================================================= */

    private boolean tieneXml(
            String archivoXmlNombre,
            String hashXml
    ) {

        return
                archivoXmlNombre != null
                        &&
                        !archivoXmlNombre.isBlank()

                        &&

                        hashXml != null
                        &&
                        !hashXml.isBlank();
    }


    /* =========================================================
       RANGO FECHAS
       ========================================================= */

    private void validarRangoFechas(
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        if (
                fechaDesde != null
                        &&
                        fechaHasta != null
                        &&
                        fechaDesde.isAfter(
                                fechaHasta
                        )
        ) {

            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser mayor a la fecha final."
            );
        }
    }



    private record EstadoRegistro(

            LocalDate fechaMovimiento,

            LocalDate fechaProyectada,

            LocalDate fechaPago,

            Boolean cancelado

    ) {
    }


    private record EstadoActualizacion(

            LocalDate fechaMovimiento,

            LocalDate fechaProyectada

    ) {
    }
}