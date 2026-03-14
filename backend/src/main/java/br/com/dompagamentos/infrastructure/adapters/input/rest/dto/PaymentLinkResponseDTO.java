package br.com.dompagamentos.infrastructure.adapters.input.rest.dto;

import br.com.dompagamentos.application.ports.input.CreatePaymentLinkInputPort;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentLinkResponseDTO(
        String id,
        String url,
        String name,
        BigDecimal amountInCents,
        String description,
        LocalDate expiresAt
) {
    public static PaymentLinkResponseDTO from(CreatePaymentLinkInputPort.Result result) {
        return new PaymentLinkResponseDTO(
                result.pspLinkId(),
                result.url(),
                result.name(),
                result.amountInCents(),
                result.description(),
                result.expiresAt()
        );
    }
}
