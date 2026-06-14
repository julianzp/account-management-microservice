package com.julian.account_movement_service.application.dto.cuenta;

import com.julian.account_movement_service.domain.model.TipoCuenta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateCuentaRequest(

        @NotNull(message = "El tipo de cuenta es obligatorio")
        TipoCuenta tipoCuenta,

        @NotNull(message = "El saldo inicial es obligatorio")
        @DecimalMin(value = "0.00", message = "El saldo inicial no puede ser negativo")
        BigDecimal saldoInicial,

        @NotNull(message = "El estado es obligatorio")
        Boolean estado,

        @NotNull(message = "El clienteId es obligatorio")
        Long clienteId
) {
}