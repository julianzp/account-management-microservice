package com.julian.client_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class Persona {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Genero genero;

    @Column(nullable = false)
    private Integer edad;

    @Column(nullable = false, unique = true, length = 50)
    private String identificacion;

    @Column(nullable = false, length = 150)
    private String direccion;

    @Column(nullable = false, length = 30)
    private String telefono;
}