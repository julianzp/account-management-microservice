package com.julian.client_service.infrastructure.messaging;

import com.julian.client_service.application.port.out.ClienteEventPublisherPort;
import com.julian.client_service.config.ClientEventsMessagingProperties;
import com.julian.client_service.domain.model.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQClienteEventPublisher implements ClienteEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;
    private final ClientEventsMessagingProperties properties;

    @Override
    public void publishClientCreated(Cliente cliente) {
        rabbitTemplate.convertAndSend(
                properties.exchange(),
                properties.createdRoutingKey(),
                ClienteIntegrationEvent.from(cliente)
        );
    }

    @Override
    public void publishClientUpdated(Cliente cliente) {
        rabbitTemplate.convertAndSend(
                properties.exchange(),
                properties.updatedRoutingKey(),
                ClienteIntegrationEvent.from(cliente)
        );
    }
}