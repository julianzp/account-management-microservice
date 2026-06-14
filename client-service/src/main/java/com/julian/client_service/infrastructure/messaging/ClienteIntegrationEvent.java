package com.julian.client_service.infrastructure.messaging;

import com.julian.client_service.domain.model.Cliente;

public record ClienteIntegrationEvent(
        Long clienteId,
        String nombre,
        String identificacion,
        Boolean estado
) {

    public static ClienteIntegrationEvent from(Cliente cliente) {
        return new ClienteIntegrationEvent(
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getIdentificacion(),
                cliente.getEstado()
        );
    }
}