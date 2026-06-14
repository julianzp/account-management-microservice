package com.julian.account_movement_service.application.dto.movimiento;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateMovimientoRequest(

        @NotBlank(message = "El número de cuenta es obligatorio")
        String numeroCuenta,

        @PastOrPresent(message = "La fecha no puede ser futura")
        LocalDate fecha,

        @NotNull(message = "El valor del movimiento es obligatorio")
        @Digits(integer = 13, fraction = 2, message = "El valor debe tener máximo 13 enteros y 2 decimales")
        BigDecimal valor
) {
}