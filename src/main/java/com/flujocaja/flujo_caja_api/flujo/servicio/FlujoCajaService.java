package com.flujocaja.flujo_caja_api.flujo.servicio;

import com.flujocaja.flujo_caja_api.flujo.dto.FlujoCajaResponse;
import com.flujocaja.flujo_caja_api.flujo.repositorio.FlujoCajaRepository;
import com.flujocaja.flujo_caja_api.seguridad.servicio.ContextoSeguridad;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FlujoCajaService {

    private final FlujoCajaRepository flujoCajaRepository;

    private final ContextoSeguridad contextoSeguridad;


    public FlujoCajaService(
            FlujoCajaRepository flujoCajaRepository,
            ContextoSeguridad contextoSeguridad
    ) {

        this.flujoCajaRepository =
                flujoCajaRepository;

        this.contextoSeguridad =
                contextoSeguridad;
    }


    public FlujoCajaResponse obtener(
            LocalDate fechaDesde,
            LocalDate fechaHasta
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


        return flujoCajaRepository.obtener(

                contextoSeguridad.empresaId(),

                fechaDesde,

                fechaHasta
        );
    }
}