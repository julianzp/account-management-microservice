package com.julian.account_movement_service.application.dto.reporte;

import com.julian.account_movement_service.domain.model.TipoCuenta;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteEstadoCuentaResponse(
        LocalDate fecha,
        String cliente,
        String numeroCuenta,
        TipoCuenta tipo,
        BigDecimal saldoInicial,
        Boolean estado,
        BigDecimal movimiento,
        BigDecimal saldoDisponible
) {
}