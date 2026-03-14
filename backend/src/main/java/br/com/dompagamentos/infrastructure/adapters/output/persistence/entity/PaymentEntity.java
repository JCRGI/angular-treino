package br.com.dompagamentos.infrastructure.adapters.output.persistence.entity;

import br.com.dompagamentos.domain.model.enums.PaymentMethod;
import br.com.dompagamentos.domain.model.enums.PaymentStatus;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_merchant", columnList = "merchant_id"),
        @Index(name = "idx_payment_psp_id", columnList = "psp_payment_id"),
        @Index(name = "idx_payment_status", columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "customer_name", length = 200)
    private String customerName;

    @Column(name = "customer_email", length = 200)
    private String customerEmail;

    @Column(name = "customer_document", length = 18)
    private String customerDocument;

    @Column(name = "amount_cents", nullable = false, precision = 20, scale = 2)
    private BigDecimal amountInCents;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 20)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "psp_provider", length = 10)
    private PspProvider pspProvider;

    @Column(name = "psp_payment_id", length = 100)
    private String pspPaymentId;

    @Column(name = "psp_payment_url", length = 1000)
    private String pspPaymentUrl;

    @Column(name = "psp_pix_qr_code", columnDefinition = "TEXT")
    private String pspPixQrCode;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "installments")
    private Integer installments;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
