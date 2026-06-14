package com.julian.account_movement_service.application.dto.movimiento;

import com.julian.account_movement_service.domain.model.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoResponse(
        Long id,
        LocalDate fecha,
        TipoMovimiento tipoMovimiento,
        BigDecimal valor,
        BigDecimal saldoDisponible,
        String numeroCuenta,
        Long cuentaId
) {
}