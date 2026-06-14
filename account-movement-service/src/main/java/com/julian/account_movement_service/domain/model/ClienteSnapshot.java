package com.julian.account_movement_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cliente_snapshots")
public class ClienteSnapshot {

    @Id
    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String identificacion;

    @Column(nullable = false)
    private Boolean estado;
}