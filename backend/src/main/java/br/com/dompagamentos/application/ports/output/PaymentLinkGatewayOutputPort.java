package br.com.dompagamentos.application.ports.output;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Secondary Port — cria payment links no PSP.
 * Diferente de uma cobrança convencional, o link não está vinculado a um
 * cliente específico: o pagador acessa a URL e escolhe o método de pagamento.
 *
 * Implementado pelo AsaasGatewayAdapter.
 */
public interface PaymentLinkGatewayOutputPort {

    PaymentLinkResult createPaymentLink(String merchantExternalId, Command command);

    record Command(
            String name,
            BigDecimal amountInCents,   // null = cliente informa o valor
            String description,
            LocalDate expiresAt
    ) {}

    record PaymentLinkResult(
            String pspLinkId,
            String url
    ) {}
}
