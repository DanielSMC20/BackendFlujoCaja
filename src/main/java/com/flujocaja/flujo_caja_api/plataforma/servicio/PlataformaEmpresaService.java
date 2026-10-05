package com.flujocaja.flujo_caja_api.plataforma.servicio;

import com.flujocaja.flujo_caja_api.comun.excepcion.RecursoNoEncontradoException;

import com.flujocaja.flujo_caja_api.plataforma.dto.EmpresaPlataformaActualizarRequest;
import com.flujocaja.flujo_caja_api.plataforma.dto.EmpresaPlataformaCrearRequest;
import com.flujocaja.flujo_caja_api.plataforma.dto.EmpresaPlataformaResponse;

import com.flujocaja.flujo_caja_api.plataforma.repositorio.PlataformaRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlataformaEmpresaService {

    private final PlataformaRepository plataformaRepository;

    private final PasswordEncoder passwordEncoder;


    public PlataformaEmpresaService(

            PlataformaRepository plataformaRepository,

            PasswordEncoder passwordEncoder

    ) {

        this.plataformaRepository =
                plataformaRepository;

        this.passwordEncoder =
                passwordEncoder;
    }


    public List<EmpresaPlataformaResponse> listar(
            String busqueda
    ) {

        String filtro =
                busqueda == null
                        ||
                        busqueda.isBlank()

                        ? null

                        : busqueda.trim();


        return plataformaRepository
                .listarEmpresas(
                        filtro
                );
    }


    public EmpresaPlataformaResponse obtener(
            Integer empresaId
    ) {

        return plataformaRepository

                .obtenerEmpresa(
                        empresaId
                )

                .orElseThrow(

                        () ->
                                new RecursoNoEncontradoException(
                                        "La empresa no existe."
                                )
                );
    }


    public EmpresaPlataformaResponse crear(
            EmpresaPlataformaCrearRequest request
    ) {

        var administrador =
                request.administrador();


        /*
         * El frontend nunca manda un hash.
         * El hash se genera aquí.
         */
        String passwordHash =
                passwordEncoder.encode(
                        administrador.passwordTemporal()
                );


        Integer empresaId =
                plataformaRepository
                        .crearEmpresa(

                                request.ruc().trim(),

                                request.razonSocial().trim(),

                                request.nombreComercial().trim(),

                                request.monedaBase(),

                                request.zonaHoraria().trim(),

                                administrador
                                        .correo()
                                        .trim()
                                        .toLowerCase(),

                                administrador
                                        .nombres()
                                        .trim(),

                                administrador
                                        .apellidos()
                                        .trim(),

                                passwordHash
                        );


        return obtener(
                empresaId
        );
    }


    public EmpresaPlataformaResponse actualizar(

            Integer empresaId,

            EmpresaPlataformaActualizarRequest request

    ) {

        obtener(
                empresaId
        );


        plataformaRepository
                .actualizarEmpresa(

                        empresaId,

                        request.ruc().trim(),

                        request.razonSocial().trim(),

                        request.nombreComercial().trim(),

                        request.monedaBase(),

                        request.zonaHoraria().trim()
                );


        return obtener(
                empresaId
        );
    }
}