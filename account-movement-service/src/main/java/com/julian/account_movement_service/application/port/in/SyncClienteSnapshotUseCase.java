package com.julian.account_movement_service.application.port.in;

import com.julian.account_movement_service.application.dto.SyncClienteSnapshotCommand;

public interface SyncClienteSnapshotUseCase {

    void sync(SyncClienteSnapshotCommand command);
}