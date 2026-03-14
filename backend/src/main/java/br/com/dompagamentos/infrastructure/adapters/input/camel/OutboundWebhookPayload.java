package br.com.dompagamentos.infrastructure.adapters.input.camel;

import java.math.BigDecimal;

/**
 * Payload enviado ao merchant quando o status de um pagamento muda.
 * Contrato público — qualquer alteração aqui quebra a integração dos merchants.
 */
public record OutboundWebhookPayload(
        String event,
        String paymentId,
        String merchantId,
        String status,
        BigDecimal amountInCents,
        String pspPaymentId,
        String updatedAt
) {}
