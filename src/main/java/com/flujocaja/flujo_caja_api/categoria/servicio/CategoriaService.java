package com.flujocaja.flujo_caja_api.categoria.servicio;

import com.flujocaja.flujo_caja_api.categoria.dto.CategoriaActualizarRequest;
import com.flujocaja.flujo_caja_api.categoria.dto.CategoriaCrearRequest;
import com.flujocaja.flujo_caja_api.categoria.dto.CategoriaResponse;
import com.flujocaja.flujo_caja_api.categoria.repositorio.CategoriaRepository;
import com.flujocaja.flujo_caja_api.seguridad.servicio.ContextoSeguridad;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ContextoSeguridad contextoSeguridad;


    public CategoriaService(
            CategoriaRepository categoriaRepository,
            ContextoSeguridad contextoSeguridad
    ) {

        this.categoriaRepository =
                categoriaRepository;

        this.contextoSeguridad =
                contextoSeguridad;
    }

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR', 'CONTADOR', 'OPERADOR', 'CONSULTA')"
    )
    public List<CategoriaResponse> listar(
            Integer tipoMovimiento,
            Boolean soloActivos
    ) {

        return categoriaRepository.listar(

                contextoSeguridad.empresaId(),

                tipoMovimiento,

                soloActivos
        );
    }

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR', 'CONTADOR', 'OPERADOR')"
    )
    public CategoriaResponse registrar(
            CategoriaCrearRequest request
    ) {

        return categoriaRepository.registrar(

                contextoSeguridad.empresaId(),

                request.tipoMovimiento(),

                request.nombre(),

                request.descripcion(),

                contextoSeguridad.usuarioId()
        );
    }

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR', 'CONTADOR', 'OPERADOR')"
    )
    public CategoriaResponse actualizar(
            Integer categoriaId,
            CategoriaActualizarRequest request
    ) {

        return categoriaRepository.actualizar(

                contextoSeguridad.empresaId(),

                categoriaId,

                request.nombre(),

                request.descripcion(),

                request.activo(),

                contextoSeguridad.usuarioId()
        );
    }
}