CREATE TABLE cliente_snapshots (
                                  cliente_id BIGINT PRIMARY KEY,
                                  nombre VARCHAR(100) NOT NULL,
                                  identificacion VARCHAR(50) NOT NULL,
                                  estado BOOLEAN NOT NULL
);