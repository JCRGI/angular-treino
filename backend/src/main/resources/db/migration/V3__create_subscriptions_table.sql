-- V3: Tabela de assinaturas recorrentes
-- Gerenciadas pelo Asaas: https://docs.asaas.com/reference/criar-nova-assinatura

CREATE TABLE subscriptions (
    id                  UUID            NOT NULL,
    merchant_id         UUID            NOT NULL,
    customer_name       VARCHAR(200)    NOT NULL,
    customer_email      VARCHAR(200)    NOT NULL,
    customer_document   VARCHAR(18)     NOT NULL,
    amount_cents        NUMERIC(15, 2)  NOT NULL,
    method              VARCHAR(20)     NOT NULL,
    cycle               VARCHAR(20)     NOT NULL,
    status              VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    next_due_date       DATE,
    description         VARCHAR(500),
    psp_provider        VARCHAR(20),
    psp_subscription_id VARCHAR(100),
    created_at          TIMESTAMP       NOT NULL,
    updated_at          TIMESTAMP       NOT NULL,

    CONSTRAINT pk_subscriptions PRIMARY KEY (id),
    CONSTRAINT fk_subscriptions_merchant FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);

CREATE INDEX idx_subscriptions_merchant_id ON subscriptions (merchant_id);
CREATE INDEX idx_subscriptions_status      ON subscriptions (status);
CREATE INDEX idx_subscriptions_created_at  ON subscriptions (created_at DESC);
