package br.com.dompagamentos.domain.model;

import br.com.dompagamentos.domain.exception.DomainException;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa uma API Key para autenticação B2B.
 * O valor em texto plano nunca é armazenado — só o hash SHA-256.
 *
 * O campo visiblePrefix (ex: "dpag_a3f9b2...") permite que o usuário
 * identifique qual chave está no painel sem revelar o valor completo.
 */
public class MerchantApiKey {

    private final UUID id;
    private final UUID merchantId;
    private final String keyHash;        // SHA-256 da chave real
    private final String visiblePrefix;  // Exibição: "dpag_a3f9b2..."
    private String description;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;

    public static MerchantApiKey create(UUID merchantId, String keyHash,
                                        String visiblePrefix, String description) {
        if (merchantId == null) throw new DomainException("MerchantId é obrigatório");
        if (keyHash == null || keyHash.isBlank()) throw new DomainException("Hash da chave é obrigatório");
        return new MerchantApiKey(merchantId, keyHash, visiblePrefix, description);
    }

    private MerchantApiKey(UUID merchantId, String keyHash, String visiblePrefix, String description) {
        this.id = UUID.randomUUID();
        this.merchantId = merchantId;
        this.keyHash = keyHash;
        this.visiblePrefix = visiblePrefix;
        this.description = description;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    // Reconstrução do repositório
    public MerchantApiKey(UUID id, UUID merchantId, String keyHash, String visiblePrefix,
                          String description, boolean active,
                          LocalDateTime createdAt, LocalDateTime lastUsedAt) {
        this.id = id;
        this.merchantId = merchantId;
        this.keyHash = keyHash;
        this.visiblePrefix = visiblePrefix;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.lastUsedAt = lastUsedAt;
    }

    public void revoke() {
        this.active = false;
    }

    public void recordUsage() {
        this.lastUsedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getMerchantId() { return merchantId; }
    public String getKeyHash() { return keyHash; }
    public String getVisiblePrefix() { return visiblePrefix; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastUsedAt() { return lastUsedAt; }
}
