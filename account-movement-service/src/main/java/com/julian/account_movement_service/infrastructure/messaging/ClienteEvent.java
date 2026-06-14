package com.julian.account_movement_service.infrastructure.messaging;

public record ClienteEvent(
        Long clienteId,
        String nombre,
        String identificacion,
        Boolean estado
) {
}