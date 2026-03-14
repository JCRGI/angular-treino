package br.com.dompagamentos.infrastructure.adapters.output.persistence.repository;

import br.com.dompagamentos.infrastructure.adapters.output.persistence.entity.MerchantUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantUserJpaRepository extends JpaRepository<MerchantUserEntity, UUID> {

    Optional<MerchantUserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMerchantId(UUID merchantId);
}
