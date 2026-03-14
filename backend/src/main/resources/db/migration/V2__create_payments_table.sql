-- V2: Tabela de pagamentos/cobranças
CREATE TABLE IF NOT EXISTS payments (
    id                UUID           NOT NULL,
    merchant_id       UUID           NOT NULL,
    customer_name     VARCHAR(200),
    customer_email    VARCHAR(200),
    customer_document VARCHAR(18),
    amount_cents      NUMERIC(20, 2) NOT NULL,
    method            VARCHAR(20)    NOT NULL,
    status            VARCHAR(40)    NOT NULL DEFAULT 'PENDING',
    psp_provider      VARCHAR(10),
    psp_payment_id    VARCHAR(100),
    psp_payment_url   VARCHAR(1000),
    psp_pix_qr_code   TEXT,
    description       VARCHAR(500),
    due_date          DATE,
    installments      INTEGER        NOT NULL DEFAULT 1,
    created_at        TIMESTAMP      NOT NULL,
    updated_at        TIMESTAMP,

    CONSTRAINT pk_payments PRIMARY KEY (id),
    CONSTRAINT fk_payments_merchant FOREIGN KEY (merchant_id) REFERENCES merchants (id)
);

CREATE INDEX IF NOT EXISTS idx_payment_merchant    ON payments (merchant_id);
CREATE INDEX IF NOT EXISTS idx_payment_psp_id      ON payments (psp_payment_id);
CREATE INDEX IF NOT EXISTS idx_payment_status      ON payments (status);
CREATE INDEX IF NOT EXISTS idx_payment_created_at  ON payments (created_at DESC);

COMMENT ON TABLE payments IS 'Cobranças emitidas pela plataforma Dom Pagamentos';
