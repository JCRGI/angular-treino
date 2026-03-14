package br.com.dompagamentos.infrastructure.adapters.output.persistence.entity;

import br.com.dompagamentos.domain.model.enums.PaymentMethod;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import br.com.dompagamentos.domain.model.enums.SubscriptionCycle;
import br.com.dompagamentos.domain.model.enums.SubscriptionStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions", indexes = {
        @Index(name = "idx_subscriptions_merchant_id", columnList = "merchant_id"),
        @Index(name = "idx_subscriptions_status", columnList = "status")
})
public class SubscriptionEntity {

    @Id
    private UUID id;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;

    @Column(name = "customer_email", nullable = false, length = 200)
    private String customerEmail;

    @Column(name = "customer_document", nullable = false, length = 18)
    private String customerDocument;

    @Column(name = "amount_cents", nullable = false)
    private BigDecimal amountInCents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionCycle cycle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status;

    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "psp_provider", length = 20)
    private PspProvider pspProvider;

    @Column(name = "psp_subscription_id", length = 100)
    private String pspSubscriptionId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getMerchantId() { return merchantId; }
    public void setMerchantId(UUID merchantId) { this.merchantId = merchantId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerDocument() { return customerDocument; }
    public void setCustomerDocument(String customerDocument) { this.customerDocument = customerDocument; }

    public BigDecimal getAmountInCents() { return amountInCents; }
    public void setAmountInCents(BigDecimal amountInCents) { this.amountInCents = amountInCents; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public SubscriptionCycle getCycle() { return cycle; }
    public void setCycle(SubscriptionCycle cycle) { this.cycle = cycle; }

    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }

    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public PspProvider getPspProvider() { return pspProvider; }
    public void setPspProvider(PspProvider pspProvider) { this.pspProvider = pspProvider; }

    public String getPspSubscriptionId() { return pspSubscriptionId; }
    public void setPspSubscriptionId(String pspSubscriptionId) { this.pspSubscriptionId = pspSubscriptionId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
