package br.com.dompagamentos.domain.model;

import br.com.dompagamentos.domain.exception.DomainException;
import br.com.dompagamentos.domain.model.enums.PspProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Aggregate root — representa um lojista/merchant cadastrado na plataforma.
 * Classe pura de domínio, sem anotações de framework.
 */
public class Merchant {

    private final UUID id;
    private String name;
    private String document;          // CPF ou CNPJ
    private String email;
    private String phone;
    private BigDecimal monthlyVolumeInCents;
    private PspProvider preferredPsp;
    private String pspExternalId;     // ID da subconta no PSP
    private boolean active;
    private String callbackUrl;           // URL para notificações de eventos de pagamento
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Factory method — único ponto de criação
    public static Merchant create(String name, String document, String email, String phone) {
        validate(name, document, email);
        return new Merchant(name, document, email, phone);
    }

    private Merchant(String name, String document, String email, String phone) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.document = document;
        this.email = email;
        this.phone = phone;
        this.monthlyVolumeInCents = BigDecimal.ZERO;
        this.preferredPsp = PspProvider.ASAAS;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Construtor de reconstituição (para repositório)
    public Merchant(UUID id, String name, String document, String email, String phone,
                    BigDecimal monthlyVolumeInCents, PspProvider preferredPsp,
                    String pspExternalId, boolean active,
                    LocalDateTime createdAt, LocalDateTime updatedAt,
                    String callbackUrl) {
        this.id = id;
        this.name = name;
        this.document = document;
        this.email = email;
        this.phone = phone;
        this.monthlyVolumeInCents = monthlyVolumeInCents;
        this.preferredPsp = preferredPsp;
        this.pspExternalId = pspExternalId;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.callbackUrl = callbackUrl;
    }

    public void updateMonthlyVolume(BigDecimal newVolumeInCents) {
        this.monthlyVolumeInCents = newVolumeInCents;
        this.updatedAt = LocalDateTime.now();
    }

    public void assignPsp(PspProvider psp, String externalId) {
        this.preferredPsp = psp;
        this.pspExternalId = externalId;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void registerCallbackUrl(String url) {
        this.callbackUrl = url;
        this.updatedAt = LocalDateTime.now();
    }

    private static void validate(String name, String document, String email) {
        if (name == null || name.isBlank()) throw new DomainException("Nome do merchant é obrigatório");
        if (document == null || document.isBlank()) throw new DomainException("Documento (CPF/CNPJ) é obrigatório");
        if (email == null || !email.contains("@")) throw new DomainException("E-mail inválido");
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDocument() { return document; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public BigDecimal getMonthlyVolumeInCents() { return monthlyVolumeInCents; }
    public PspProvider getPreferredPsp() { return preferredPsp; }
    public String getPspExternalId() { return pspExternalId; }
    public boolean isActive() { return active; }
    public String getCallbackUrl() { return callbackUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
