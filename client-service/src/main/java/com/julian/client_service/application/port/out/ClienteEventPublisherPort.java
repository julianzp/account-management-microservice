package com.julian.client_service.application.port.out;

import com.julian.client_service.domain.model.Cliente;

public interface ClienteEventPublisherPort {

    void publishClientCreated(Cliente cliente);

    void publishClientUpdated(Cliente cliente);
}