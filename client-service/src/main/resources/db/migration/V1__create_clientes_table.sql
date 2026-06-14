CREATE TABLE clientes (
                          cliente_id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          genero VARCHAR(20) NOT NULL,
                          edad INTEGER NOT NULL,
                          identificacion VARCHAR(50) NOT NULL UNIQUE,
                          direccion VARCHAR(150) NOT NULL,
                          telefono VARCHAR(30) NOT NULL,
                          contrasena VARCHAR(100) NOT NULL,
                          estado BOOLEAN NOT NULL
);