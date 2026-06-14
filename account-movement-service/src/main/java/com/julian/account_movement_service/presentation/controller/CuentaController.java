package com.julian.account_movement_service.presentation.controller;

import com.julian.account_movement_service.application.dto.cuenta.CreateCuentaRequest;
import com.julian.account_movement_service.application.dto.cuenta.CuentaResponse;
import com.julian.account_movement_service.application.dto.cuenta.UpdateCuentaEstadoRequest;
import com.julian.account_movement_service.application.dto.cuenta.UpdateCuentaRequest;
import com.julian.account_movement_service.application.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<CuentaResponse> create(@Valid @RequestBody CreateCuentaRequest request) {
        CuentaResponse response = cuentaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> findAll() {
        return ResponseEntity.ok(cuentaService.findAll());
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> findByNumeroCuenta(@PathVariable String numeroCuenta) {
        return ResponseEntity.ok(cuentaService.findByNumeroCuenta(numeroCuenta));
    }

    @PutMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> update(
            @PathVariable String numeroCuenta,
            @Valid @RequestBody UpdateCuentaRequest request
    ) {
        return ResponseEntity.ok(cuentaService.update(numeroCuenta, request));
    }

    @PatchMapping("/{numeroCuenta}/estado")
    public ResponseEntity<CuentaResponse> updateEstado(
            @PathVariable String numeroCuenta,
            @Valid @RequestBody UpdateCuentaEstadoRequest request
    ) {
        return ResponseEntity.ok(cuentaService.updateEstado(numeroCuenta, request.estado()));
    }

    @DeleteMapping("/{numeroCuenta}")
    public ResponseEntity<Void> delete(@PathVariable String numeroCuenta) {
        cuentaService.delete(numeroCuenta);
        return ResponseEntity.noContent().build();
    }
}