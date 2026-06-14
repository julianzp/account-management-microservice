package com.julian.account_movement_service.infrastructure.persistence.repository;

import com.julian.account_movement_service.domain.model.ClienteSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteSnapshotRepository extends JpaRepository<ClienteSnapshot, Long> {
}