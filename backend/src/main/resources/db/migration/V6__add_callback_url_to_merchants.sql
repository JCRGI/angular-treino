-- V6: Adiciona URL de callback para webhooks de saída (notificações ao merchant)
ALTER TABLE merchants ADD COLUMN callback_url VARCHAR(500);

COMMENT ON COLUMN merchants.callback_url IS 'URL do merchant para receber notificações de mudança de status de pagamentos';
