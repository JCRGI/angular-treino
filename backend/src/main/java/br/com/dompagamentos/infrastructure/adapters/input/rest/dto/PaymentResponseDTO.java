package br.com.dompagamentos.infrastructure.adapters.input.rest.dto;

import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.domain.model.enums.PaymentMethod;
import br.com.dompagamentos.domain.model.enums.PaymentStatus;
import br.com.dompagamentos.domain.model.enums.PspProvider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDTO(
        UUID id,
        UUID merchantId,
        BigDecimal amountInCents,
        PaymentMethod method,
        PaymentStatus status,
        PspProvider pspProvider,
        String paymentUrl,
        String pixQrCode,
        String description,
        LocalDate dueDate,
        LocalDateTime createdAt
) {
    public static PaymentResponseDTO from(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getMerchantId(),
                payment.getAmountInCents(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getPspProvider(),
                payment.getPspPaymentUrl(),
                payment.getPspPixQrCode(),
                payment.getDescription(),
                payment.getDueDate(),
                payment.getCreatedAt()
        );
    }
}
