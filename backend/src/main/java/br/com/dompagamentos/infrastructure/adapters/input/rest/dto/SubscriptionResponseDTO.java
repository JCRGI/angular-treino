package br.com.dompagamentos.infrastructure.adapters.input.rest.dto;

import br.com.dompagamentos.domain.model.Subscription;
import br.com.dompagamentos.domain.model.enums.PaymentMethod;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import br.com.dompagamentos.domain.model.enums.SubscriptionCycle;
import br.com.dompagamentos.domain.model.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SubscriptionResponseDTO(
        UUID id,
        UUID merchantId,
        String customerName,
        String customerEmail,
        BigDecimal amountInCents,
        PaymentMethod method,
        SubscriptionCycle cycle,
        SubscriptionStatus status,
        LocalDate nextDueDate,
        String description,
        PspProvider pspProvider,
        String pspSubscriptionId,
        LocalDateTime createdAt
) {
    public static SubscriptionResponseDTO from(Subscription s) {
        return new SubscriptionResponseDTO(
                s.getId(), s.getMerchantId(),
                s.getCustomerName(), s.getCustomerEmail(),
                s.getAmountInCents(), s.getMethod(),
                s.getCycle(), s.getStatus(), s.getNextDueDate(),
                s.getDescription(), s.getPspProvider(),
                s.getPspSubscriptionId(), s.getCreatedAt()
        );
    }
}
