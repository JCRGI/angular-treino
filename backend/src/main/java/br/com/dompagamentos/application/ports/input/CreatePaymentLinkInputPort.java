package br.com.dompagamentos.application.ports.input;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Primary Port — contrato para geração de payment links.
 *
 * Payment links diferem de cobranças normais:
 *  - Não exigem CPF do pagador no momento da criação
 *  - O pagador escolhe o método de pagamento na página do link
 *  - Podem ser compartilhados via WhatsApp / e-mail / redes sociais
 */
public interface CreatePaymentLinkInputPort {

    Result execute(Command command);

    record Command(
            UUID merchantId,
            String name,
            BigDecimal amountInCents,
            String description,
            LocalDate expiresAt
    ) {}

    record Result(
            String pspLinkId,
            String url,
            String name,
            BigDecimal amountInCents,
            String description,
            LocalDate expiresAt
    ) {}
}
