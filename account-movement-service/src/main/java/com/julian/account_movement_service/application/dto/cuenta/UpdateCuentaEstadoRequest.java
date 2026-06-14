package com.julian.account_movement_service.application.dto.cuenta;

import jakarta.validation.constraints.NotNull;

public record UpdateCuentaEstadoRequest(

        @NotNull(message = "El estado es obligatorio")
        Boolean estado
) {
}