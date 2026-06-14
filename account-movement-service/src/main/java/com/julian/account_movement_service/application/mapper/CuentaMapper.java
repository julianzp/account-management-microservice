package com.julian.account_movement_service.application.mapper;

import com.julian.account_movement_service.application.dto.cuenta.CreateCuentaRequest;
import com.julian.account_movement_service.application.dto.cuenta.CuentaResponse;
import com.julian.account_movement_service.application.dto.cuenta.UpdateCuentaRequest;
import com.julian.account_movement_service.domain.model.Cuenta;
import org.springframework.stereotype.Component;

@Component
public class CuentaMapper {

    public Cuenta toEntity(CreateCuentaRequest request) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(request.numeroCuenta());
        cuenta.setTipoCuenta(request.tipoCuenta());
        cuenta.setSaldoInicial(request.saldoInicial());
        cuenta.setSaldoDisponible(request.saldoInicial());
        cuenta.setEstado(request.estado());
        cuenta.setClienteId(request.clienteId());
        return cuenta;
    }

    public void updateEntity(Cuenta cuenta, UpdateCuentaRequest request) {
        cuenta.setTipoCuenta(request.tipoCuenta());
        cuenta.setSaldoInicial(request.saldoInicial());
        cuenta.setEstado(request.estado());
        cuenta.setClienteId(request.clienteId());
    }

    public CuentaResponse toResponse(Cuenta cuenta) {
        return new CuentaResponse(
                cuenta.getId(),
                cuenta.getNumeroCuenta(),
                cuenta.getTipoCuenta(),
                cuenta.getSaldoInicial(),
                cuenta.getSaldoDisponible(),
                cuenta.getEstado(),
                cuenta.getClienteId()
        );
    }
}