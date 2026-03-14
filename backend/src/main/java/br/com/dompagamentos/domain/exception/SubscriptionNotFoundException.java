package br.com.dompagamentos.domain.exception;

import java.util.UUID;

public class SubscriptionNotFoundException extends DomainException {

    public SubscriptionNotFoundException(UUID id) {
        super("Assinatura não encontrada: " + id);
    }

    public SubscriptionNotFoundException(String pspSubscriptionId) {
        super("Assinatura não encontrada para id externo: " + pspSubscriptionId);
    }
}
