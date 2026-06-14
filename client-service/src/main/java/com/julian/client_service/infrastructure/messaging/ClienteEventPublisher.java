package com.julian.client_service.infrastructure.messaging;

import com.julian.client_service.config.RabbitMQConfig;
import com.julian.client_service.domain.model.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishClientCreated(Cliente cliente) {
        ClienteEvent event = toEvent(cliente);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CLIENT_EXCHANGE,
                RabbitMQConfig.CLIENT_CREATED_ROUTING_KEY,
                event
        );
    }

    public void publishClientUpdated(Cliente cliente) {
        ClienteEvent event = toEvent(cliente);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CLIENT_EXCHANGE,
                RabbitMQConfig.CLIENT_UPDATED_ROUTING_KEY,
                event
        );
    }

    private ClienteEvent toEvent(Cliente cliente) {
        return new ClienteEvent(
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getIdentificacion(),
                cliente.getEstado()
        );
    }
}