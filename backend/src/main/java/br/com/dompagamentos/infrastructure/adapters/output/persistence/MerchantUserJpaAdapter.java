package br.com.dompagamentos.infrastructure.adapters.output.persistence;

import br.com.dompagamentos.application.ports.output.MerchantUserRepositoryOutputPort;
import br.com.dompagamentos.domain.model.MerchantUser;
import br.com.dompagamentos.infrastructure.adapters.output.persistence.entity.MerchantUserEntity;
import br.com.dompagamentos.infrastructure.adapters.output.persistence.repository.MerchantUserJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class MerchantUserJpaAdapter implements MerchantUserRepositoryOutputPort {

    private final MerchantUserJpaRepository repository;

    public MerchantUserJpaAdapter(MerchantUserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public MerchantUser save(MerchantUser user) {
        return toDomain(repository.save(toEntity(user)));
    }

    @Override
    public Optional<MerchantUser> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByMerchantId(UUID merchantId) {
        return repository.existsByMerchantId(merchantId);
    }

    private MerchantUserEntity toEntity(MerchantUser u) {
        MerchantUserEntity e = new MerchantUserEntity();
        e.setId(u.getId());
        e.setMerchantId(u.getMerchantId());
        e.setEmail(u.getEmail());
        e.setPasswordHash(u.getPasswordHash());
        e.setActive(u.isActive());
        e.setCreatedAt(u.getCreatedAt());
        e.setUpdatedAt(u.getUpdatedAt() != null ? u.getUpdatedAt() : LocalDateTime.now());
        return e;
    }

    private MerchantUser toDomain(MerchantUserEntity e) {
        return new MerchantUser(e.getId(), e.getMerchantId(), e.getEmail(),
                e.getPasswordHash(), e.isActive(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
