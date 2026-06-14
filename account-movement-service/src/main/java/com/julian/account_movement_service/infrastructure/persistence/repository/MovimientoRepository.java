package com.julian.account_movement_service.infrastructure.persistence.repository;

import com.julian.account_movement_service.domain.model.Movimiento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    @Override
    @EntityGraph(attributePaths = "cuenta")
    List<Movimiento> findAll();

    @Override
    @EntityGraph(attributePaths = "cuenta")
    Optional<Movimiento> findById(Long id);

    @Query("""
            SELECT m
            FROM Movimiento m
            JOIN FETCH m.cuenta c
            WHERE c.clienteId = :clienteId
              AND m.fecha BETWEEN :fechaInicio AND :fechaFin
            ORDER BY m.fecha ASC, m.id ASC
            """)
    List<Movimiento> findMovimientosForReporte(
            @Param("clienteId") Long clienteId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}