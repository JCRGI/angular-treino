-- V1: Tabela de merchants (lojistas)
CREATE TABLE IF NOT EXISTS merchants (
    id                    UUID         NOT NULL,
    name                  VARCHAR(200) NOT NULL,
    document              VARCHAR(18)  NOT NULL UNIQUE,
    email                 VARCHAR(200) NOT NULL,
    phone                 VARCHAR(20),
    monthly_volume_cents  NUMERIC(20, 2) NOT NULL DEFAULT 0,
    preferred_psp         VARCHAR(20)  NOT NULL DEFAULT 'ASAAS',
    psp_external_id       VARCHAR(100),
    active                BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP    NOT NULL,
    updated_at            TIMESTAMP,

    CONSTRAINT pk_merchants PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_merchant_document ON merchants (document);
CREATE INDEX IF NOT EXISTS idx_merchant_email    ON merchants (email);

COMMENT ON TABLE merchants IS 'Lojistas cadastrados na plataforma Dom Pagamentos';
