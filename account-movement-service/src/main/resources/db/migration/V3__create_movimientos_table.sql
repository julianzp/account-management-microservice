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