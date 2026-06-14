package com.julian.account_movement_service.application.service;

import com.julian.account_movement_service.application.dto.movimiento.CreateMovimientoRequest;
import com.julian.account_movement_service.application.dto.movimiento.MovimientoResponse;
import com.julian.account_movement_service.application.mapper.MovimientoMapper;
import com.julian.account_movement_service.domain.exception.BusinessRuleException;
import com.julian.account_movement_service.domain.exception.InsufficientBalanceException;
import com.julian.account_movement_service.domain.exception.ResourceNotFoundException;
import com.julian.account_movement_service.domain.model.Cuenta;
import com.julian.account_movement_service.domain.model.Movimiento;
import com.julian.account_movement_service.infrastructure.persistence.repository.CuentaRepository;
import com.julian.account_movement_service.infrastructure.persistence.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;

    public MovimientoResponse create(CreateMovimientoRequest request) {
        BigDecimal valor = normalizeAndValidateValor(request.valor());

        Cuenta cuenta = cuentaRepository.findByNumeroCuentaForUpdate(request.numeroCuenta())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + request.numeroCuenta()));

        validateCuentaActiva(cuenta);

        BigDecimal nuevoSaldoDisponible = cuenta.getSaldoDisponible().add(valor);

        validateSaldoDisponible(nuevoSaldoDisponible);

        cuenta.setSaldoDisponible(nuevoSaldoDisponible);

        LocalDate fecha = request.fecha() != null ? request.fecha() : LocalDate.now();

        Movimiento movimiento = movimientoMapper.toEntity(
                cuenta,
                fecha,
                valor,
                nuevoSaldoDisponible
        );

        movimientoRepository.save(movimiento);
        cuentaRepository.save(cuenta);

        return movimientoMapper.toResponse(movimiento);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> findAll() {
        return movimientoRepository.findAll()
                .stream()
                .map(movimientoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimientoResponse findById(Long movimientoId) {
        Movimiento movimiento = findEntityById(movimientoId);
        return movimientoMapper.toResponse(movimiento);
    }

    private Movimiento findEntityById(Long movimientoId) {
        return movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + movimientoId));
    }

    private void validateCuentaActiva(Cuenta cuenta) {
        if (Boolean.FALSE.equals(cuenta.getEstado())) {
            throw new BusinessRuleException("No se pueden registrar movimientos en una cuenta inactiva");
        }
    }

    private BigDecimal normalizeAndValidateValor(BigDecimal valor) {
        BigDecimal normalizedValor = valor.setScale(2, RoundingMode.HALF_UP);

        if (normalizedValor.compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessRuleException("El valor del movimiento no puede ser cero");
        }

        return normalizedValor;
    }

    private void validateSaldoDisponible(BigDecimal saldoDisponible) {
        if (saldoDisponible.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Saldo no disponible");
        }
    }
}