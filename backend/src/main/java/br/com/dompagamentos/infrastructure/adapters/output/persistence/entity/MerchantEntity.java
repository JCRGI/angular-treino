package br.com.dompagamentos.infrastructure.adapters.output.persistence.entity;

import br.com.dompagamentos.domain.model.enums.PspProvider;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "merchants", indexes = {
        @Index(name = "idx_merchant_document", columnList = "document", unique = true),
        @Index(name = "idx_merchant_email", columnList = "email")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MerchantEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "document", nullable = false, length = 18, unique = true)
    private String document;

    @Column(name = "email", nullable = false, length = 200)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "monthly_volume_cents", precision = 20, scale = 2)
    private BigDecimal monthlyVolumeInCents;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_psp", length = 20)
    private PspProvider preferredPsp;

    @Column(name = "psp_external_id", length = 100)
    private String pspExternalId;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "callback_url", length = 500)
    private String callbackUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
