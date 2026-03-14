package br.com.dompagamentos.infrastructure.adapters.output.persistence.repository;

import br.com.dompagamentos.infrastructure.adapters.output.persistence.entity.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantJpaRepository extends JpaRepository<MerchantEntity, UUID> {
    Optional<MerchantEntity> findByDocument(String document);
    boolean existsByDocument(String document);
}
