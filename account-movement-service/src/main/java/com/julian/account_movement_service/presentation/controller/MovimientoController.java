package com.julian.account_movement_service.presentation.controller;

import com.julian.account_movement_service.application.dto.movimiento.CreateMovimientoRequest;
import com.julian.account_movement_service.application.dto.movimiento.MovimientoResponse;
import com.julian.account_movement_service.application.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    public ResponseEntity<MovimientoResponse> create(@Valid @RequestBody CreateMovimientoRequest request) {
        MovimientoResponse response = movimientoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> findAll() {
        return ResponseEntity.ok(movimientoService.findAll());
    }

    @GetMapping("/{movimientoId}")
    public ResponseEntity<MovimientoResponse> findById(@PathVariable Long movimientoId) {
        return ResponseEntity.ok(movimientoService.findById(movimientoId));
    }

}