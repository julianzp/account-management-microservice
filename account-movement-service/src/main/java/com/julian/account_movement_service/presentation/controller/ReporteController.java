package com.julian.account_movement_service.presentation.controller;

import com.julian.account_movement_service.application.dto.reporte.ReporteEstadoCuentaResponse;
import com.julian.account_movement_service.application.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<List<ReporteEstadoCuentaResponse>> generarEstadoCuenta(
            @RequestParam Long clienteId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFin
    ) {
        return ResponseEntity.ok(
                reporteService.generarEstadoCuenta(clienteId, fechaInicio, fechaFin)
        );
    }
}