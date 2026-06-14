package com.julian.client_service.application.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateEstadoRequest(
        @NotNull(message = "El estado es obligatorio")
        Boolean estado
) {
}