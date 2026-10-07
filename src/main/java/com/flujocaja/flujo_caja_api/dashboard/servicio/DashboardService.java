package com.flujocaja.flujo_caja_api.dashboard.servicio;

import com.flujocaja.flujo_caja_api.dashboard.dto.DashboardResponse;
import com.flujocaja.flujo_caja_api.dashboard.dto.DashboardResumenAnualResponse;

import com.flujocaja.flujo_caja_api.dashboard.repositorio.DashboardRepository;

import com.flujocaja.flujo_caja_api.seguridad.servicio.ContextoSeguridad;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    private final ContextoSeguridad contextoSeguridad;


    public DashboardService(
            DashboardRepository dashboardRepository,
            ContextoSeguridad contextoSeguridad
    ) {

        this.dashboardRepository =
                dashboardRepository;

        this.contextoSeguridad =
                contextoSeguridad;
    }


    /* =========================================================
       DASHBOARD ACTUAL
       ========================================================= */

    public DashboardResponse obtener(

            LocalDate fechaDesde,

            LocalDate fechaHasta,

            Integer cantidadUltimos
    ) {

        if (
                fechaDesde != null

                        && fechaHasta != null

                        && fechaDesde.isAfter(
                        fechaHasta
                )
        ) {

            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser mayor a la fecha final."
            );
        }


        if (
                cantidadUltimos == null

                        || cantidadUltimos < 1

                        || cantidadUltimos > 50
        ) {

            throw new IllegalArgumentException(
                    "La cantidad de últimos movimientos debe estar entre 1 y 50."
            );
        }


        return dashboardRepository.obtener(

                contextoSeguridad.empresaId(),

                fechaDesde,

                fechaHasta,

                cantidadUltimos
        );
    }

    public List<DashboardResumenAnualResponse> obtenerResumenAnual(

            Integer anioActual
    ) {

        if (
                anioActual == null

                        || anioActual < 2000

                        || anioActual > 2100
        ) {

            throw new IllegalArgumentException(
                    "El año consultado no es válido."
            );
        }


        return dashboardRepository.obtenerResumenAnual(

                contextoSeguridad.empresaId(),

                anioActual
        );
    }
}