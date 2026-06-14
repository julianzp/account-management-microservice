CREATE DATABASE client_db;
CREATE DATABASE account_db;

\connect client_db;

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

\connect account_db;

CREATE TABLE client_snapshots (
    cliente_id BIGINT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    identificacion VARCHAR(50) NOT NULL,
    estado BOOLEAN NOT NULL
);

CREATE TABLE cuentas (
    id BIGSERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(30) NOT NULL UNIQUE,
    tipo_cuenta VARCHAR(30) NOT NULL,
    saldo_inicial NUMERIC(15, 2) NOT NULL,
    saldo_disponible NUMERIC(15, 2) NOT NULL,
    estado BOOLEAN NOT NULL,
    cliente_id BIGINT NOT NULL
);

CREATE TABLE movimientos (
    id BIGSERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    tipo_movimiento VARCHAR(30) NOT NULL,
    valor NUMERIC(15, 2) NOT NULL,
    saldo_disponible NUMERIC(15, 2) NOT NULL,
    cuenta_id BIGINT NOT NULL,

    CONSTRAINT fk_movimientos_cuenta
        FOREIGN KEY (cuenta_id)
        REFERENCES cuentas(id)
);

CREATE INDEX idx_movimientos_cuenta_id ON movimientos(cuenta_id);
CREATE INDEX idx_movimientos_fecha ON movimientos(fecha);