package com.julian.account_movement_service.application.dto.cuenta;

import com.julian.account_movement_service.domain.model.TipoCuenta;

import java.math.BigDecimal;

public record CuentaResponse(
        Long id,
        String numeroCuenta,
        TipoCuenta tipoCuenta,
        BigDecimal saldoInicial,
        BigDecimal saldoDisponible,
        Boolean estado,
        Long clienteId
) {
}