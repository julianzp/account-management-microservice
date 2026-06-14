package com.julian.client_service.application.dto;

import com.julian.client_service.domain.model.Genero;

public record ClienteResponse(
        Long clienteId,
        String nombre,
        Genero genero,
        Integer edad,
        String identificacion,
        String direccion,
        String telefono,
        Boolean estado
) {
}