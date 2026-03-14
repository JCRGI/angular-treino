package br.com.dompagamentos.infrastructure.adapters.output.persistence.repository;

import br.com.dompagamentos.infrastructure.adapters.output.persistence.entity.SubscriptionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionEntity, UUID> {

    List<SubscriptionEntity> findByMerchantIdOrderByCreatedAtDesc(UUID merchantId, Pageable pageable);
}
