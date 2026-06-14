package com.julian.client_service.presentation.controller;

import com.julian.client_service.application.dto.ClienteResponse;
import com.julian.client_service.application.dto.CreateClienteRequest;
import com.julian.client_service.application.dto.UpdateClienteRequest;
import com.julian.client_service.application.dto.UpdateEstadoRequest;
import com.julian.client_service.application.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> create(@Valid @RequestBody CreateClienteRequest request) {
        ClienteResponse response = clienteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> findAll() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable Long clienteId) {
        return ResponseEntity.ok(clienteService.findById(clienteId));
    }

    @PutMapping("/{clienteId}")
    public ResponseEntity<ClienteResponse> update(
            @PathVariable Long clienteId,
            @Valid @RequestBody UpdateClienteRequest request
    ) {
        return ResponseEntity.ok(clienteService.update(clienteId, request));
    }

    @PatchMapping("/{clienteId}/estado")
    public ResponseEntity<ClienteResponse> updateEstado(
            @PathVariable Long clienteId,
            @Valid @RequestBody UpdateEstadoRequest request
    ) {
        return ResponseEntity.ok(clienteService.updateEstado(clienteId, request.estado()));
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> delete(@PathVariable Long clienteId) {
        clienteService.delete(clienteId);
        return ResponseEntity.noContent().build();
    }
}