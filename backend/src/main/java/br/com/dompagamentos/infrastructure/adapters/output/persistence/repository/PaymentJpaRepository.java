package br.com.dompagamentos.infrastructure.adapters.output.persistence.repository;

import br.com.dompagamentos.infrastructure.adapters.output.persistence.entity.PaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByPspPaymentId(String pspPaymentId);
    Page<PaymentEntity> findByMerchantIdOrderByCreatedAtDesc(UUID merchantId, Pageable pageable);
}
