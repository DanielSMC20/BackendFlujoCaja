package com.flujocaja.flujo_caja_api.reporte.servicio;

import com.flujocaja.flujo_caja_api.reporte.dto.ReporteMovimientoResponse;
import com.flujocaja.flujo_caja_api.reporte.repositorio.ReporteMovimientoRepository;
import com.flujocaja.flujo_caja_api.seguridad.servicio.ContextoSeguridad;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReporteMovimientoService {

    private final ReporteMovimientoRepository reporteMovimientoRepository;

    private final ContextoSeguridad contextoSeguridad;


    public ReporteMovimientoService(
            ReporteMovimientoRepository reporteMovimientoRepository,
            ContextoSeguridad contextoSeguridad
    ) {

        this.reporteMovimientoRepository =
                reporteMovimientoRepository;

        this.contextoSeguridad =
                contextoSeguridad;
    }


    public ReporteMovimientoResponse obtener(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Integer tipoMovimiento,
            Integer categoriaId,
            Boolean cancelado,
            Integer origenRegistro,
            Boolean incluirAnulados
    ) {

        if (
                fechaDesde != null
                        &&
                        fechaHasta != null
                        &&
                        fechaDesde.isAfter(fechaHasta)
        ) {

            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser mayor a la fecha final."
            );
        }


        /*
         * El estado cancelado corresponde a egresos.
         *
         * Si explícitamente solicitan ingresos,
         * no debe enviarse un estado de egreso.
         */
        if (
                Integer.valueOf(1).equals(tipoMovimiento)
                        &&
                        cancelado != null
        ) {

            throw new IllegalArgumentException(
                    "El estado de pago solo aplica a los egresos."
            );
        }


        return reporteMovimientoRepository.obtener(

                contextoSeguridad.empresaId(),

                fechaDesde,

                fechaHasta,

                tipoMovimiento,

                categoriaId,

                cancelado,

                origenRegistro,

                Boolean.TRUE.equals(
                        incluirAnulados
                )
        );
    }
}