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

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public CategoriaResponse registrar(
            CategoriaCrearRequest request
    ) {

        if (
                !Integer.valueOf(2).equals(
                        request.tipoMovimiento()
                )
        ) {

            throw new IllegalArgumentException(
                    "Las categorías configurables son únicamente las de egresos."
            );
        }

        return categoriaRepository.registrar(
                contextoSeguridad.empresaId(),
                request.tipoMovimiento(),
                request.nombre(),
                request.descripcion(),
                contextoSeguridad.usuarioId()
        );
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public CategoriaResponse actualizar(
            Integer categoriaId,
            CategoriaActualizarRequest request
    ) {

        Integer empresaId =
                contextoSeguridad.empresaId();

        boolean esCategoriaDeEgreso =
                categoriaRepository
                        .listar(
                                empresaId,
                                2,
                                false
                        )
                        .stream()
                        .anyMatch(
                                categoria ->
                                        categoriaId.equals(
                                                categoria.id()
                                        )
                        );

        if (!esCategoriaDeEgreso) {

            throw new IllegalArgumentException(
                    "Solo pueden editarse categorías de egresos de esta empresa."
            );
        }

        return categoriaRepository.actualizar(
                empresaId,
                categoriaId,
                request.nombre(),
                request.descripcion(),
                request.activo(),
                contextoSeguridad.usuarioId()
        );
    }
}