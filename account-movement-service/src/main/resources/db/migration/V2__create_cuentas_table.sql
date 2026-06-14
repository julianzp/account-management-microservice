CREATE TABLE cuentas (
                         id BIGSERIAL PRIMARY KEY,
                         numero_cuenta VARCHAR(30) NOT NULL UNIQUE,
                         tipo_cuenta VARCHAR(30) NOT NULL,
                         saldo_inicial NUMERIC(15, 2) NOT NULL,
                         saldo_disponible NUMERIC(15, 2) NOT NULL,
                         estado BOOLEAN NOT NULL,
                         cliente_id BIGINT NOT NULL
);