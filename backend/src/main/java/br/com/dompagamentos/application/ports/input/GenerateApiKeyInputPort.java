package br.com.dompagamentos.application.ports.input;

import br.com.dompagamentos.domain.model.MerchantApiKey;

import java.util.UUID;

public interface GenerateApiKeyInputPort {

    GenerateApiKeyResult execute(Command command);

    record Command(UUID merchantId, String description) {}

    /** O plaintextKey é retornado UMA única vez — o caller deve exibir e nunca rearmazenar. */
    record GenerateApiKeyResult(String plaintextKey, MerchantApiKey apiKey) {}
}
