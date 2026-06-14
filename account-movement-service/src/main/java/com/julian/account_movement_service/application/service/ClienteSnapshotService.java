package com.julian.account_movement_service.application.service;

import com.julian.account_movement_service.application.dto.SyncClienteSnapshotCommand;
import com.julian.account_movement_service.application.port.in.SyncClienteSnapshotUseCase;
import com.julian.account_movement_service.domain.model.ClienteSnapshot;
import com.julian.account_movement_service.infrastructure.persistence.repository.ClienteSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteSnapshotService implements SyncClienteSnapshotUseCase {

    private final ClienteSnapshotRepository clienteSnapshotRepository;

    @Override
    @Transactional
    public void sync(SyncClienteSnapshotCommand command) {
        ClienteSnapshot snapshot = clienteSnapshotRepository
                .findById(command.clienteId())
                .orElseGet(ClienteSnapshot::new);

        snapshot.setClienteId(command.clienteId());
        snapshot.setNombre(command.nombre());
        snapshot.setIdentificacion(command.identificacion());
        snapshot.setEstado(command.estado());

        clienteSnapshotRepository.save(snapshot);
    }
}