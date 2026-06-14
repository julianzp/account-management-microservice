package com.julian.account_movement_service.infrastructure.messaging;

import com.julian.account_movement_service.application.dto.SyncClienteSnapshotCommand;

public record ClienteIntegrationEvent(
        Long clienteId,
        String nombre,
        String identificacion,
        Boolean estado
) {

    public SyncClienteSnapshotCommand toCommand() {
        return new SyncClienteSnapshotCommand(
                clienteId,
                nombre,
                identificacion,
                estado
        );
    }
}