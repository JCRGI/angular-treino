package br.com.dompagamentos.application.ports.output;

import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.domain.model.Subscription;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Secondary Port — interface que o domínio/aplicação usa para falar com PSPs.
 * Implementada pelos adapters Asaas e iugu na camada de infraestrutura.
 */
public interface PaymentGatewayOutputPort {

    /** Cria a cobrança no PSP e retorna os dados externos (ID, URL, QR code). */
    GatewayResponse createPayment(Payment payment, Merchant merchant);

    /** Cria subconta (white-label) do merchant no PSP. */
    String createSubAccount(Merchant merchant);

    /** Solicita estorno de um pagamento já recebido. */
    void refundPayment(String pspPaymentId);

    /** Cria uma assinatura recorrente no PSP. */
    SubscriptionGatewayResponse createSubscription(Subscription subscription, Merchant merchant);

    /** Cancela uma assinatura no PSP. */
    void cancelSubscription(String pspSubscriptionId);

    /** Retorna o saldo disponível na conta do PSP, em centavos. */
    BigDecimal getBalance();

    record GatewayResponse(
            String pspPaymentId,
            String paymentUrl,
            String pixQrCode,
            String pixQrCodeBase64
    ) {}

    record SubscriptionGatewayResponse(
            String pspSubscriptionId,
            String pspStatus,
            LocalDate nextDueDate
    ) {}
}
