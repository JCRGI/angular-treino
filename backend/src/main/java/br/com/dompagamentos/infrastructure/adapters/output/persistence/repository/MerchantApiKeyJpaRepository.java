package br.com.dompagamentos.infrastructure.adapters.output.persistence.repository;

import br.com.dompagamentos.infrastructure.adapters.output.persistence.entity.MerchantApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantApiKeyJpaRepository extends JpaRepository<MerchantApiKeyEntity, UUID> {

    Optional<MerchantApiKeyEntity> findByKeyHashAndActiveTrue(String keyHash);

    Optional<MerchantApiKeyEntity> findByIdAndMerchantId(UUID id, UUID merchantId);

    List<MerchantApiKeyEntity> findByMerchantIdOrderByCreatedAtDesc(UUID merchantId);
}
