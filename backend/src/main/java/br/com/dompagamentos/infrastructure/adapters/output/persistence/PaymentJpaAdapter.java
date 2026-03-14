package br.com.dompagamentos.infrastructure.adapters.output.persistence;

import br.com.dompagamentos.application.ports.output.PaymentRepositoryOutputPort;
import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.infrastructure.adapters.output.persistence.entity.PaymentEntity;
import br.com.dompagamentos.infrastructure.adapters.output.persistence.repository.PaymentJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentJpaAdapter implements PaymentRepositoryOutputPort {

    private final PaymentJpaRepository repository;

    public PaymentJpaAdapter(PaymentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment save(Payment payment) {
        return toDomain(repository.save(toEntity(payment)));
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Payment> findByPspPaymentId(String pspPaymentId) {
        return repository.findByPspPaymentId(pspPaymentId).map(this::toDomain);
    }

    @Override
    public List<Payment> findByMerchantId(UUID merchantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByMerchantIdOrderByCreatedAtDesc(merchantId, pageable)
                .stream().map(this::toDomain).toList();
    }

    private PaymentEntity toEntity(Payment p) {
        return PaymentEntity.builder()
                .id(p.getId()).merchantId(p.getMerchantId())
                .customerName(p.getCustomerName()).customerEmail(p.getCustomerEmail())
                .customerDocument(p.getCustomerDocument()).amountInCents(p.getAmountInCents())
                .method(p.getMethod()).status(p.getStatus()).pspProvider(p.getPspProvider())
                .pspPaymentId(p.getPspPaymentId()).pspPaymentUrl(p.getPspPaymentUrl())
                .pspPixQrCode(p.getPspPixQrCode()).description(p.getDescription())
                .dueDate(p.getDueDate()).installments(p.getInstallments())
                .createdAt(p.getCreatedAt()).updatedAt(p.getUpdatedAt())
                .build();
    }

    private Payment toDomain(PaymentEntity e) {
        return new Payment(
                e.getId(), e.getMerchantId(), e.getCustomerName(), e.getCustomerEmail(),
                e.getCustomerDocument(), e.getAmountInCents(), e.getMethod(), e.getStatus(),
                e.getPspProvider(), e.getPspPaymentId(), e.getPspPaymentUrl(), e.getPspPixQrCode(),
                e.getDescription(), e.getDueDate(), e.getInstallments(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
