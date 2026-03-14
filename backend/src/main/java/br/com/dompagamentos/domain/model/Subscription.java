package br.com.dompagamentos.domain.model;

import br.com.dompagamentos.domain.exception.DomainException;
import br.com.dompagamentos.domain.model.enums.PaymentMethod;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import br.com.dompagamentos.domain.model.enums.SubscriptionCycle;
import br.com.dompagamentos.domain.model.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Aggregate root — representa uma assinatura recorrente gerenciada pelo Asaas.
 * O Asaas gera cobranças automaticamente conforme o ciclo definido.
 */
public class Subscription {

    private final UUID id;
    private final UUID merchantId;
    private String customerName;
    private String customerEmail;
    private String customerDocument;
    private BigDecimal amountInCents;
    private PaymentMethod method;
    private SubscriptionCycle cycle;
    private SubscriptionStatus status;
    private LocalDate nextDueDate;
    private String description;
    private PspProvider pspProvider;
    private String pspSubscriptionId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Subscription create(UUID merchantId, String customerName, String customerEmail,
                                      String customerDocument, BigDecimal amountInCents,
                                      PaymentMethod method, SubscriptionCycle cycle,
                                      LocalDate nextDueDate, String description) {
        validate(merchantId, amountInCents, method, cycle, nextDueDate);
        return new Subscription(merchantId, customerName, customerEmail, customerDocument,
                amountInCents, method, cycle, nextDueDate, description);
    }

    private Subscription(UUID merchantId, String customerName, String customerEmail,
                         String customerDocument, BigDecimal amountInCents,
                         PaymentMethod method, SubscriptionCycle cycle,
                         LocalDate nextDueDate, String description) {
        this.id = UUID.randomUUID();
        this.merchantId = merchantId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerDocument = customerDocument;
        this.amountInCents = amountInCents;
        this.method = method;
        this.cycle = cycle;
        this.nextDueDate = nextDueDate;
        this.description = description;
        this.status = SubscriptionStatus.ACTIVE;
        this.pspProvider = PspProvider.ASAAS;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Construtor de reconstituição
    public Subscription(UUID id, UUID merchantId, String customerName, String customerEmail,
                        String customerDocument, BigDecimal amountInCents, PaymentMethod method,
                        SubscriptionCycle cycle, SubscriptionStatus status, LocalDate nextDueDate,
                        String description, PspProvider pspProvider, String pspSubscriptionId,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.merchantId = merchantId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerDocument = customerDocument;
        this.amountInCents = amountInCents;
        this.method = method;
        this.cycle = cycle;
        this.status = status;
        this.nextDueDate = nextDueDate;
        this.description = description;
        this.pspProvider = pspProvider;
        this.pspSubscriptionId = pspSubscriptionId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Valida que a assinatura pertence ao merchantId informado.
     * Garante isolamento de tenant sem precisar de autenticação.
     */
    public void assertOwnedBy(UUID expectedMerchantId) {
        if (!this.merchantId.equals(expectedMerchantId)) {
            throw new br.com.dompagamentos.domain.exception.ResourceAccessDeniedException(
                    "assinatura", this.id);
        }
    }

    public void confirmWithPspData(String pspSubscriptionId, LocalDate nextDueDate) {
        this.pspSubscriptionId = pspSubscriptionId;
        this.nextDueDate = nextDueDate;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == SubscriptionStatus.CANCELLED) {
            throw new DomainException("Assinatura já está cancelada.");
        }
        this.status = SubscriptionStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    private static void validate(UUID merchantId, BigDecimal amountInCents,
                                  PaymentMethod method, SubscriptionCycle cycle, LocalDate nextDueDate) {
        if (merchantId == null) throw new DomainException("MerchantId é obrigatório");
        if (amountInCents == null || amountInCents.compareTo(BigDecimal.ZERO) <= 0)
            throw new DomainException("Valor da assinatura deve ser maior que zero");
        if (method == null) throw new DomainException("Método de pagamento é obrigatório");
        if (cycle == null) throw new DomainException("Ciclo da assinatura é obrigatório");
        if (nextDueDate == null || nextDueDate.isBefore(LocalDate.now()))
            throw new DomainException("Data do próximo vencimento deve ser hoje ou no futuro");
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getMerchantId() { return merchantId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerDocument() { return customerDocument; }
    public BigDecimal getAmountInCents() { return amountInCents; }
    public PaymentMethod getMethod() { return method; }
    public SubscriptionCycle getCycle() { return cycle; }
    public SubscriptionStatus getStatus() { return status; }
    public LocalDate getNextDueDate() { return nextDueDate; }
    public String getDescription() { return description; }
    public PspProvider getPspProvider() { return pspProvider; }
    public String getPspSubscriptionId() { return pspSubscriptionId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
