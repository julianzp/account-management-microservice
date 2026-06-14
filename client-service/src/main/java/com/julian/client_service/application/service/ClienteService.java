package com.julian.client_service.application.service;

import com.julian.client_service.application.dto.ClienteResponse;
import com.julian.client_service.application.dto.CreateClienteRequest;
import com.julian.client_service.application.dto.UpdateClienteRequest;
import com.julian.client_service.application.mapper.ClienteMapper;
import com.julian.client_service.domain.exception.DuplicateResourceException;
import com.julian.client_service.domain.exception.ResourceNotFoundException;
import com.julian.client_service.domain.model.Cliente;
import com.julian.client_service.infrastructure.messaging.ClienteEventPublisher;
import com.julian.client_service.infrastructure.persistence.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ClienteEventPublisher clienteEventPublisher;

    @Transactional
    public ClienteResponse create(CreateClienteRequest request) {
        if (clienteRepository.existsByIdentificacion(request.identificacion())) {
            throw new DuplicateResourceException("Ya existe un cliente con la identificación: " + request.identificacion());
        }

        Cliente cliente = clienteMapper.toEntity(request);
        Cliente savedCliente = clienteRepository.save(cliente);

        clienteEventPublisher.publishClientCreated(savedCliente);

        return clienteMapper.toResponse(savedCliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> findAll() {
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse findById(Long clienteId) {
        Cliente cliente = findEntityById(clienteId);
        return clienteMapper.toResponse(cliente);
    }

    @Transactional
    public ClienteResponse update(Long clienteId, UpdateClienteRequest request) {
        Cliente cliente = findEntityById(clienteId);

        clienteMapper.updateEntity(cliente, request);

        Cliente updatedCliente = clienteRepository.save(cliente);

        clienteEventPublisher.publishClientUpdated(updatedCliente);

        return clienteMapper.toResponse(updatedCliente);
    }

    @Transactional
    public ClienteResponse updateEstado(Long clienteId, Boolean estado) {
        Cliente cliente = findEntityById(clienteId);

        cliente.setEstado(estado);

        Cliente updatedCliente = clienteRepository.save(cliente);

        clienteEventPublisher.publishClientUpdated(updatedCliente);

        return clienteMapper.toResponse(updatedCliente);
    }

    @Transactional
    public void delete(Long clienteId) {
        Cliente cliente = findEntityById(clienteId);

        cliente.setEstado(false);

        Cliente updatedCliente = clienteRepository.save(cliente);

        clienteEventPublisher.publishClientUpdated(updatedCliente);
    }

    private Cliente findEntityById(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + clienteId));
    }
}