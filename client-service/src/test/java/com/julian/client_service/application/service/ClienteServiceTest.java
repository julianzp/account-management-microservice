package com.julian.client_service.application.service;

import com.julian.client_service.application.dto.ClienteResponse;
import com.julian.client_service.application.dto.CreateClienteRequest;
import com.julian.client_service.application.dto.UpdateClienteRequest;
import com.julian.client_service.application.mapper.ClienteMapper;
import com.julian.client_service.domain.exception.ResourceNotFoundException;
import com.julian.client_service.domain.model.Cliente;
import com.julian.client_service.domain.model.Genero;
import com.julian.client_service.infrastructure.persistence.repository.ClienteRepository;
import com.julian.client_service.infrastructure.messaging.RabbitMQClienteEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private RabbitMQClienteEventPublisher rabbitMQClienteEventPublisher;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;
    private ClienteResponse clienteResponse;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setClienteId(1L);
        cliente.setNombre("Julian");
        cliente.setGenero(Genero.valueOf("MASCULINO"));
        cliente.setEdad(30);
        cliente.setIdentificacion("123456789");
        cliente.setDireccion("Calle 123");
        cliente.setTelefono("3001234567");
        cliente.setContrasena("1234");
        cliente.setEstado(true);

        clienteResponse = new ClienteResponse(
                1L,
                "Julian",
                Genero.MASCULINO,
                30,
                "123456789",
                "Calle 123",
                "3001234567",
                true
        );
    }

    @Test
    void shouldCreateClienteSuccessfully() {
        CreateClienteRequest request = new CreateClienteRequest(
                "Julian",
                Genero.MASCULINO,
                30,
                "123456789",
                "Calle 123",
                "3001234567",
                "1234",
                true
        );

        when(clienteMapper.toEntity(request)).thenReturn(cliente);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toResponse(cliente)).thenReturn(clienteResponse);

        ClienteResponse result = clienteService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.clienteId()).isEqualTo(1L);
        assertThat(result.nombre()).isEqualTo("Julian");

        verify(clienteRepository).save(cliente);
        verify(rabbitMQClienteEventPublisher).publishClientCreated(cliente);
    }

    @Test
    void shouldFindClienteByIdSuccessfully() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponse(cliente)).thenReturn(clienteResponse);

        ClienteResponse result = clienteService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.clienteId()).isEqualTo(1L);
        assertThat(result.nombre()).isEqualTo("Julian");

        verify(clienteRepository).findById(1L);
        verify(clienteMapper).toResponse(cliente);
        verifyNoInteractions(rabbitMQClienteEventPublisher);
    }

    @Test
    void shouldThrowExceptionWhenClienteDoesNotExist() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(clienteRepository).findById(99L);
        verifyNoInteractions(clienteMapper);
        verifyNoInteractions(rabbitMQClienteEventPublisher);
    }

    @Test
    void shouldUpdateClienteSuccessfully() {
        UpdateClienteRequest request = new UpdateClienteRequest(
                "Julian Actualizado",
                Genero.MASCULINO,
                31,
                "123456789",
                "Nueva direccion",
                "3010000000",
                "5678",
                true
        );

        ClienteResponse updatedResponse = new ClienteResponse(
                1L,
                "Julian Actualizado",
                Genero.MASCULINO,
                31,
                "123456789",
                "Nueva direccion",
                "3010000000",
                true
        );

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toResponse(cliente)).thenReturn(updatedResponse);

        ClienteResponse result = clienteService.update(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.clienteId()).isEqualTo(1L);
        assertThat(result.nombre()).isEqualTo("Julian Actualizado");

        verify(clienteRepository).findById(1L);
        verify(clienteMapper).updateEntity(cliente, request);
        verify(clienteRepository).save(cliente);
        verify(rabbitMQClienteEventPublisher).publishClientUpdated(cliente);
    }

}