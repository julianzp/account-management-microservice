package com.julian.account_movement_service.application.service;

import com.julian.account_movement_service.application.dto.cuenta.CreateCuentaRequest;
import com.julian.account_movement_service.application.dto.cuenta.CuentaResponse;
import com.julian.account_movement_service.application.dto.cuenta.UpdateCuentaRequest;
import com.julian.account_movement_service.application.mapper.CuentaMapper;
import com.julian.account_movement_service.domain.exception.BusinessRuleException;
import com.julian.account_movement_service.domain.exception.DuplicateResourceException;
import com.julian.account_movement_service.domain.exception.ResourceNotFoundException;
import com.julian.account_movement_service.domain.model.ClienteSnapshot;
import com.julian.account_movement_service.domain.model.Cuenta;
import com.julian.account_movement_service.infrastructure.persistence.repository.ClienteSnapshotRepository;
import com.julian.account_movement_service.infrastructure.persistence.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteSnapshotRepository clienteSnapshotRepository;
    private final CuentaMapper cuentaMapper;

    public CuentaResponse create(CreateCuentaRequest request) {
        validateClienteActivo(request.clienteId());

        if (cuentaRepository.existsByNumeroCuenta(request.numeroCuenta())) {
            throw new DuplicateResourceException("Ya existe una cuenta con el número: " + request.numeroCuenta());
        }

        Cuenta cuenta = cuentaMapper.toEntity(request);
        Cuenta savedCuenta = cuentaRepository.save(cuenta);

        return cuentaMapper.toResponse(savedCuenta);
    }

    @Transactional(readOnly = true)
    public List<CuentaResponse> findAll() {
        return cuentaRepository.findAll()
                .stream()
                .map(cuentaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CuentaResponse findByNumeroCuenta(String numeroCuenta) {
        Cuenta cuenta = findEntityByNumeroCuenta(numeroCuenta);
        return cuentaMapper.toResponse(cuenta);
    }

    public CuentaResponse update(String numeroCuenta, UpdateCuentaRequest request) {
        validateClienteActivo(request.clienteId());

        Cuenta cuenta = findEntityByNumeroCuenta(numeroCuenta);

        cuentaMapper.updateEntity(cuenta, request);

        Cuenta updatedCuenta = cuentaRepository.save(cuenta);

        return cuentaMapper.toResponse(updatedCuenta);
    }

    public CuentaResponse updateEstado(String numeroCuenta, Boolean estado) {
        Cuenta cuenta = findEntityByNumeroCuenta(numeroCuenta);

        cuenta.setEstado(estado);

        Cuenta updatedCuenta = cuentaRepository.save(cuenta);

        return cuentaMapper.toResponse(updatedCuenta);
    }

    public void delete(String numeroCuenta) {
        Cuenta cuenta = findEntityByNumeroCuenta(numeroCuenta);

        cuenta.setEstado(false);

        cuentaRepository.save(cuenta);
    }

    private Cuenta findEntityByNumeroCuenta(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + numeroCuenta));
    }

    private void validateClienteActivo(Long clienteId) {
        ClienteSnapshot clientSnapshot = clienteSnapshotRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + clienteId));

        if (Boolean.FALSE.equals(clientSnapshot.getEstado())) {
            throw new BusinessRuleException("No se puede crear o actualizar la cuenta porque el cliente está inactivo");
        }
    }
}