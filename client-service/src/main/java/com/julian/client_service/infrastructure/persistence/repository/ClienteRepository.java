package com.julian.client_service.infrastructure.persistence.repository;

import com.julian.client_service.domain.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByIdentificacion(String identificacion);
}