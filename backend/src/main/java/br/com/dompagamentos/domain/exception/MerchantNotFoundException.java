package br.com.dompagamentos.domain.exception;

import java.util.UUID;

public class MerchantNotFoundException extends DomainException {

    public MerchantNotFoundException(UUID id) {
        super("Merchant não encontrado: " + id);
    }

    public MerchantNotFoundException(String document) {
        super("Merchant não encontrado pelo documento: " + document);
    }
}
