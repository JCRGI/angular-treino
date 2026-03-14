-- V4: Usuários de dashboard dos merchants (autenticação JWT)
CREATE TABLE merchant_users (
    id            UUID         NOT NULL,
    merchant_id   UUID         NOT NULL,
    email         VARCHAR(200) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL,

    CONSTRAINT pk_merchant_users PRIMARY KEY (id),
    CONSTRAINT uq_merchant_users_email UNIQUE (email),
    CONSTRAINT fk_merchant_users_merchant FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);

CREATE INDEX idx_merchant_users_merchant_id ON merchant_users (merchant_id);
