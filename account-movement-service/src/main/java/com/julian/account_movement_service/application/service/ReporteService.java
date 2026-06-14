package com.julian.account_movement_service.application.service;

import com.julian.account_movement_service.application.dto.reporte.ReporteEstadoCuentaResponse;
import com.julian.account_movement_service.domain.exception.BusinessRuleException;
import com.julian.account_movement_service.domain.exception.ResourceNotFoundException;
import com.julian.account_movement_service.domain.model.ClienteSnapshot;
import com.julian.account_movement_service.domain.model.Movimiento;
import com.julian.account_movement_service.infrastructure.persistence.repository.ClienteSnapshotRepository;
import com.julian.account_movement_service.infrastructure.persistence.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final ClienteSnapshotRepository clienteSnapshotRepository;
    private final MovimientoRepository movimientoRepository;

    public List<ReporteEstadoCuentaResponse> generarEstadoCuenta(
            Long clienteId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        validateFechas(fechaInicio, fechaFin);

        ClienteSnapshot cliente = clienteSnapshotRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + clienteId));

        List<Movimiento> movimientos = movimientoRepository.findMovimientosForReporte(
                clienteId,
                fechaInicio,
                fechaFin
        );

        return movimientos.stream()
                .map(movimiento -> new ReporteEstadoCuentaResponse(
                        movimiento.getFecha(),
                        cliente.getNombre(),
                        movimiento.getCuenta().getNumeroCuenta(),
                        movimiento.getCuenta().getTipoCuenta(),
                        movimiento.getCuenta().getSaldoInicial(),
                        movimiento.getCuenta().getEstado(),
                        movimiento.getValor(),
                        movimiento.getSaldoDisponible()
                ))
                .toList();
    }

    private void validateFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessRuleException("La fechaInicio y la fechaFin son obligatorias");
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessRuleException("La fechaInicio no puede ser mayor que la fechaFin");
        }
    }
}