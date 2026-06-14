package com.julian.client_service.application.mapper;

import com.julian.client_service.application.dto.ClienteResponse;
import com.julian.client_service.application.dto.CreateClienteRequest;
import com.julian.client_service.application.dto.UpdateClienteRequest;
import com.julian.client_service.domain.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(CreateClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNombre(request.nombre());
        cliente.setGenero(request.genero());
        cliente.setEdad(request.edad());
        cliente.setIdentificacion(request.identificacion());
        cliente.setDireccion(request.direccion());
        cliente.setTelefono(request.telefono());
        cliente.setContrasena(request.contrasena());
        cliente.setEstado(request.estado());
        return cliente;
    }

    public void updateEntity(Cliente cliente, UpdateClienteRequest request) {
        cliente.setNombre(request.nombre());
        cliente.setGenero(request.genero());
        cliente.setEdad(request.edad());
        cliente.setIdentificacion(request.identificacion());
        cliente.setDireccion(request.direccion());
        cliente.setTelefono(request.telefono());
        cliente.setContrasena(request.contrasena());
        cliente.setEstado(request.estado());
    }

    public ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getGenero(),
                cliente.getEdad(),
                cliente.getIdentificacion(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                cliente.getEstado()
        );
    }
}