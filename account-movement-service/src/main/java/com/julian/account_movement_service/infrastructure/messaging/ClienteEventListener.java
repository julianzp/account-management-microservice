package com.julian.account_movement_service.infrastructure.messaging;

import com.julian.account_movement_service.config.RabbitMQConfig;
import com.julian.account_movement_service.domain.model.ClienteSnapshot;
import com.julian.account_movement_service.infrastructure.persistence.repository.ClienteSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ClienteEventListener {

    private final ClienteSnapshotRepository clientSnapshotRepository;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.CLIENT_CREATED_QUEUE)
    public void handleClientCreated(ClienteEvent event) {
        saveOrUpdateSnapshot(event);
    }

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.CLIENT_UPDATED_QUEUE)
    public void handleClientUpdated(ClienteEvent event) {
        saveOrUpdateSnapshot(event);
    }

    private void saveOrUpdateSnapshot(ClienteEvent event) {
        ClienteSnapshot snapshot = clientSnapshotRepository
                .findById(event.clienteId())
                .orElseGet(ClienteSnapshot::new);

        snapshot.setClienteId(event.clienteId());
        snapshot.setNombre(event.nombre());
        snapshot.setIdentificacion(event.identificacion());
        snapshot.setEstado(event.estado());

        clientSnapshotRepository.save(snapshot);
    }
}