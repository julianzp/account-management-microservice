package com.julian.account_movement_service.application.dto;

public record SyncClienteSnapshotCommand(
        Long clienteId,
        String nombre,
        String identificacion,
        Boolean estado
) {
}