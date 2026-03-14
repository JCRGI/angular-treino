package br.com.dompagamentos.infrastructure.adapters.output.persistence;

import br.com.dompagamentos.application.ports.output.MerchantRepositoryOutputPort;
import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.infrastructure.adapters.output.persistence.entity.MerchantEntity;
import br.com.dompagamentos.infrastructure.adapters.output.persistence.repository.MerchantJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter de saída — implementa a port de repositório usando JPA.
 * O domínio e a aplicação nunca conhecem JPA, apenas esta interface.
 */
@Component
public class MerchantJpaAdapter implements MerchantRepositoryOutputPort {

    private final MerchantJpaRepository repository;

    public MerchantJpaAdapter(MerchantJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Merchant save(Merchant merchant) {
        MerchantEntity entity = toEntity(merchant);
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<Merchant> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Merchant> findByDocument(String document) {
        return repository.findByDocument(document).map(this::toDomain);
    }

    @Override
    public boolean existsByDocument(String document) {
        return repository.existsByDocument(document);
    }

    // ===== Mappers =====

    private MerchantEntity toEntity(Merchant m) {
        return MerchantEntity.builder()
                .id(m.getId())
                .name(m.getName())
                .document(m.getDocument())
                .email(m.getEmail())
                .phone(m.getPhone())
                .monthlyVolumeInCents(m.getMonthlyVolumeInCents())
                .preferredPsp(m.getPreferredPsp())
                .pspExternalId(m.getPspExternalId())
                .active(m.isActive())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    private Merchant toDomain(MerchantEntity e) {
        return new Merchant(
                e.getId(), e.getName(), e.getDocument(), e.getEmail(), e.getPhone(),
                e.getMonthlyVolumeInCents(), e.getPreferredPsp(), e.getPspExternalId(),
                e.isActive(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
