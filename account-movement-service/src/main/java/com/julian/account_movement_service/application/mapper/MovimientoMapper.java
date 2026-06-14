package com.julian.account_movement_service.application.mapper;

import com.julian.account_movement_service.application.dto.movimiento.MovimientoResponse;
import com.julian.account_movement_service.domain.model.Cuenta;
import com.julian.account_movement_service.domain.model.Movimiento;
import com.julian.account_movement_service.domain.model.TipoMovimiento;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class MovimientoMapper {

    public Movimiento toEntity(
            Cuenta cuenta,
            LocalDate fecha,
            BigDecimal valor,
            BigDecimal saldoDisponible
    ) {
        Movimiento movimiento = new Movimiento();
        movimiento.setCuenta(cuenta);
        movimiento.setFecha(fecha);
        movimiento.setValor(valor);
        movimiento.setTipoMovimiento(resolveTipoMovimiento(valor));
        movimiento.setSaldoDisponible(saldoDisponible);
        return movimiento;
    }

    public MovimientoResponse toResponse(Movimiento movimiento) {
        return new MovimientoResponse(
                movimiento.getId(),
                movimiento.getFecha(),
                movimiento.getTipoMovimiento(),
                movimiento.getValor(),
                movimiento.getSaldoDisponible(),
                movimiento.getCuenta().getNumeroCuenta(),
                movimiento.getCuenta().getId()
        );
    }

    public TipoMovimiento resolveTipoMovimiento(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            return TipoMovimiento.DEPOSITO;
        }

        return TipoMovimiento.RETIRO;
    }
}