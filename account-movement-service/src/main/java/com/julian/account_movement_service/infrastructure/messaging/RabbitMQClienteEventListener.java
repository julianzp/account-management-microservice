package com.julian.account_movement_service.infrastructure.messaging;

import com.julian.account_movement_service.application.port.in.SyncClienteSnapshotUseCase;
import com.julian.account_movement_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQClienteEventListener {

    private final SyncClienteSnapshotUseCase syncClienteSnapshotUseCase;

    @RabbitListener(queues = "${app.messaging.client-events.created-queue}")
    public void handleClientCreated(ClienteIntegrationEvent event) {
        syncClienteSnapshotUseCase.sync(event.toCommand());
    }

    @RabbitListener(queues = "${app.messaging.client-events.created-queue}")
    public void handleClientUpdated(ClienteIntegrationEvent event) {
        syncClienteSnapshotUseCase.sync(event.toCommand());
    }
}