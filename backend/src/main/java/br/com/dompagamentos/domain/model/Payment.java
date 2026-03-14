package br.com.dompagamentos.domain.model;

import br.com.dompagamentos.domain.exception.DomainException;
import br.com.dompagamentos.domain.model.enums.PaymentMethod;
import br.com.dompagamentos.domain.model.enums.PaymentStatus;
import br.com.dompagamentos.domain.model.enums.PspProvider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Aggregate root — representa uma cobrança emitida pela plataforma.
 * Contém toda a lógica de transição de estados do pagamento.
 */
public class Payment {

    private final UUID id;
    private final UUID merchantId;
    private String customerName;
    private String customerEmail;
    private String customerDocument;
    private BigDecimal amountInCents;
    private PaymentMethod method;
    private PaymentStatus status;
    private PspProvider pspProvider;
    private String pspPaymentId;        // ID externo no PSP
    private String pspPaymentUrl;       // URL de pagamento (boleto/Pix)
    private String pspPixQrCode;        // QR code Pix
    private String description;
    private LocalDate dueDate;
    private Integer installments;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Payment create(UUID merchantId, String customerName, String customerEmail,
                                 String customerDocument, BigDecimal amountInCents,
                                 PaymentMethod method, String description, LocalDate dueDate) {
        validate(merchantId, amountInCents, method);
        return new Payment(merchantId, customerName, customerEmail, customerDocument,
                amountInCents, method, description, dueDate);
    }

    private Payment(UUID merchantId, String customerName, String customerEmail,
                    String customerDocument, BigDecimal amountInCents,
                    PaymentMethod method, String description, LocalDate dueDate) {
        this.id = UUID.randomUUID();
        this.merchantId = merchantId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerDocument = customerDocument;
        this.amountInCents = amountInCents;
        this.method = method;
        this.description = description;
        this.dueDate = dueDate;
        this.installments = 1;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Construtor de reconstituição
    public Payment(UUID id, UUID merchantId, String customerName, String customerEmail,
                   String customerDocument, BigDecimal amountInCents, PaymentMethod method,
                   PaymentStatus status, PspProvider pspProvider, String pspPaymentId,
                   String pspPaymentUrl, String pspPixQrCode, String description,
                   LocalDate dueDate, Integer installments,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.merchantId = merchantId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerDocument = customerDocument;
        this.amountInCents = amountInCents;
        this.method = method;
        this.status = status;
        this.pspProvider = pspProvider;
        this.pspPaymentId = pspPaymentId;
        this.pspPaymentUrl = pspPaymentUrl;
        this.pspPixQrCode = pspPixQrCode;
        this.description = description;
        this.dueDate = dueDate;
        this.installments = installments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ===== Estado — Máquina de estados do pagamento =====

    public void startProcessing(PspProvider provider) {
        if (this.status != PaymentStatus.PENDING) {
            throw new DomainException("Apenas pagamentos PENDING podem ser processados. Status atual: " + this.status);
        }
        this.pspProvider = provider;
        this.status = PaymentStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    public void confirmWithPspData(String pspPaymentId, String paymentUrl, String pixQrCode) {
        if (this.status != PaymentStatus.PROCESSING) {
            throw new DomainException("Apenas pagamentos PROCESSING podem ser confirmados.");
        }
        this.pspPaymentId = pspPaymentId;
        this.pspPaymentUrl = paymentUrl;
        this.pspPixQrCode = pixQrCode;
        this.status = PaymentStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsReceived() {
        if (this.status != PaymentStatus.CONFIRMED) {
            throw new DomainException("Apenas pagamentos CONFIRMED podem ser recebidos.");
        }
        this.status = PaymentStatus.RECEIVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void requestRefund() {
        if (this.status != PaymentStatus.RECEIVED && this.status != PaymentStatus.CONFIRMED) {
            throw new DomainException("Apenas pagamentos recebidos ou confirmados podem ser estornados.");
        }
        this.status = PaymentStatus.REFUND_REQUESTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == PaymentStatus.RECEIVED || this.status == PaymentStatus.REFUNDED) {
            throw new DomainException("Não é possível cancelar um pagamento já recebido ou estornado.");
        }
        this.status = PaymentStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatusFromWebhook(PaymentStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    private static void validate(UUID merchantId, BigDecimal amount, PaymentMethod method) {
        if (merchantId == null) throw new DomainException("MerchantId é obrigatório");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new DomainException("Valor do pagamento deve ser maior que zero");
        if (method == null) throw new DomainException("Método de pagamento é obrigatório");
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getMerchantId() { return merchantId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerDocument() { return customerDocument; }
    public BigDecimal getAmountInCents() { return amountInCents; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }
    public PspProvider getPspProvider() { return pspProvider; }
    public String getPspPaymentId() { return pspPaymentId; }
    public String getPspPaymentUrl() { return pspPaymentUrl; }
    public String getPspPixQrCode() { return pspPixQrCode; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public Integer getInstallments() { return installments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
