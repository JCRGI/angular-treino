package br.com.dompagamentos.infrastructure.adapters.output.persistence;

import br.com.dompagamentos.application.ports.output.MerchantApiKeyRepositoryOutputPort;
import br.com.dompagamentos.domain.model.MerchantApiKey;
import br.com.dompagamentos.infrastructure.adapters.output.persistence.entity.MerchantApiKeyEntity;
import br.com.dompagamentos.infrastructure.adapters.output.persistence.repository.MerchantApiKeyJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MerchantApiKeyJpaAdapter implements MerchantApiKeyRepositoryOutputPort {

    private final MerchantApiKeyJpaRepository repository;

    public MerchantApiKeyJpaAdapter(MerchantApiKeyJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public MerchantApiKey save(MerchantApiKey apiKey) {
        return toDomain(repository.save(toEntity(apiKey)));
    }

    @Override
    public Optional<MerchantApiKey> findByKeyHash(String keyHash) {
        return repository.findByKeyHashAndActiveTrue(keyHash).map(this::toDomain);
    }

    @Override
    public Optional<MerchantApiKey> findByIdAndMerchantId(UUID id, UUID merchantId) {
        return repository.findByIdAndMerchantId(id, merchantId).map(this::toDomain);
    }

    @Override
    public List<MerchantApiKey> findByMerchantId(UUID merchantId) {
        return repository.findByMerchantIdOrderByCreatedAtDesc(merchantId)
                .stream().map(this::toDomain).toList();
    }

    private MerchantApiKeyEntity toEntity(MerchantApiKey k) {
        MerchantApiKeyEntity e = new MerchantApiKeyEntity();
        e.setId(k.getId());
        e.setMerchantId(k.getMerchantId());
        e.setKeyHash(k.getKeyHash());
        e.setVisiblePrefix(k.getVisiblePrefix());
        e.setDescription(k.getDescription());
        e.setActive(k.isActive());
        e.setCreatedAt(k.getCreatedAt());
        e.setLastUsedAt(k.getLastUsedAt());
        return e;
    }

    private MerchantApiKey toDomain(MerchantApiKeyEntity e) {
        return new MerchantApiKey(e.getId(), e.getMerchantId(), e.getKeyHash(),
                e.getVisiblePrefix(), e.getDescription(), e.isActive(),
                e.getCreatedAt(), e.getLastUsedAt());
    }
}
