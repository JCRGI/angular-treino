package br.com.dompagamentos.domain.model;

import br.com.dompagamentos.domain.exception.DomainException;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa um usuário (humano) que acessa o dashboard da plataforma.
 * Cada usuário pertence a um merchant e autentica via email + senha → JWT.
 */
public class MerchantUser {

    private final UUID id;
    private final UUID merchantId;
    private String email;
    private String passwordHash;   // bcrypt
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MerchantUser create(UUID merchantId, String email, String passwordHash) {
        if (merchantId == null) throw new DomainException("MerchantId é obrigatório");
        if (email == null || !email.contains("@")) throw new DomainException("E-mail inválido");
        if (passwordHash == null || passwordHash.isBlank()) throw new DomainException("Senha é obrigatória");
        return new MerchantUser(merchantId, email, passwordHash);
    }

    private MerchantUser(UUID merchantId, String email, String passwordHash) {
        this.id = UUID.randomUUID();
        this.merchantId = merchantId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Reconstrução do repositório
    public MerchantUser(UUID id, UUID merchantId, String email, String passwordHash,
                        boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.merchantId = merchantId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getMerchantId() { return merchantId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
