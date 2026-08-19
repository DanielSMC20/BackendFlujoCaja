package com.flujocaja.flujo_caja_api.constante.servicio;

import com.flujocaja.flujo_caja_api.constante.dto.ConstanteResponse;
import com.flujocaja.flujo_caja_api.constante.repositorio.ConstanteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConstanteService {

    private final ConstanteRepository constanteRepository;

    public ConstanteService(
            ConstanteRepository constanteRepository
    ) {
        this.constanteRepository =
                constanteRepository;
    }

    public List<ConstanteResponse> listar(
            short nConsCod
    ) {

        if (nConsCod <= 0) {
            throw new IllegalArgumentException(
                    "El código de constante debe ser mayor a cero."
            );
        }

        return constanteRepository
                .listarPorCodigo(nConsCod);
    }
}