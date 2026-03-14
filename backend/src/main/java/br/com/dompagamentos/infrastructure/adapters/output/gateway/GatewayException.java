package br.com.dompagamentos.infrastructure.adapters.output.gateway;

import br.com.dompagamentos.domain.exception.DomainException;
import br.com.dompagamentos.domain.model.enums.PspProvider;

public class GatewayException extends DomainException {

    private final PspProvider provider;

    public GatewayException(String message, PspProvider provider) {
        super("[" + provider + "] " + message);
        this.provider = provider;
    }

    public PspProvider getProvider() { return provider; }
}
