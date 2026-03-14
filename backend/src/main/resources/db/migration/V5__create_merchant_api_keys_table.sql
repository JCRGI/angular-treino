-- V5: API Keys para autenticação B2B dos merchants
CREATE TABLE merchant_api_keys (
    id             UUID         NOT NULL,
    merchant_id    UUID         NOT NULL,
    key_hash       VARCHAR(64)  NOT NULL,   -- SHA-256 da chave real
    visible_prefix VARCHAR(20)  NOT NULL,   -- Ex: "dpag_a3f9b2..."
    description    VARCHAR(200),
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL,
    last_used_at   TIMESTAMP,

    CONSTRAINT pk_merchant_api_keys PRIMARY KEY (id),
    CONSTRAINT uq_merchant_api_keys_hash UNIQUE (key_hash),
    CONSTRAINT fk_merchant_api_keys_merchant FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);

CREATE INDEX idx_api_keys_merchant_id ON merchant_api_keys (merchant_id);
