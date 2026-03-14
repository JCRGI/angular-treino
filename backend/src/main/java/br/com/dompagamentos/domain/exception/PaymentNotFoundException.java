package br.com.dompagamentos.domain.exception;

import java.util.UUID;

public class PaymentNotFoundException extends DomainException {

    public PaymentNotFoundException(UUID id) {
        super("Pagamento não encontrado: " + id);
    }

    public PaymentNotFoundException(String pspPaymentId) {
        super("Pagamento não encontrado pelo ID do PSP: " + pspPaymentId);
    }
}
