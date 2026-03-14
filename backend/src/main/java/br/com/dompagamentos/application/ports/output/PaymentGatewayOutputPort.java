package br.com.dompagamentos.application.ports.output;

import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.domain.model.Payment;

/**
 * Secondary Port — interface que o domínio/aplicação usa para falar com PSPs.
 * Implementada pelos adapters Asaas e iugu na camada de infraestrutura.
 */
public interface PaymentGatewayOutputPort {

    /**
     * Cria o pagamento no PSP e retorna os dados externos (ID, URL, QR code).
     */
    GatewayResponse createPayment(Payment payment, Merchant merchant);

    /**
     * Cria subconta do merchant no PSP.
     */
    String createSubAccount(Merchant merchant);

    /**
     * Solicita estorno de um pagamento já recebido.
     */
    void refundPayment(String pspPaymentId);

    record GatewayResponse(
            String pspPaymentId,
            String paymentUrl,
            String pixQrCode,
            String pixQrCodeBase64
    ) {}
}
