package br.com.dompagamentos.application.ports.input;

import java.util.UUID;

public interface RevokeApiKeyInputPort {

    void execute(UUID apiKeyId, UUID merchantId);
}
