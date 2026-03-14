package br.com.dompagamentos.infrastructure.adapters.output.persistence;

import br.com.dompagamentos.application.ports.output.SubscriptionRepositoryOutputPort;
import br.com.dompagamentos.domain.model.Subscription;
import br.com.dompagamentos.infrastructure.adapters.output.persistence.entity.SubscriptionEntity;
import br.com.dompagamentos.infrastructure.adapters.output.persistence.repository.SubscriptionJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SubscriptionJpaAdapter implements SubscriptionRepositoryOutputPort {

    private final SubscriptionJpaRepository repository;

    public SubscriptionJpaAdapter(SubscriptionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionEntity entity = toEntity(subscription);
        SubscriptionEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Subscription> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Subscription> findByMerchantId(UUID merchantId, int page, int size) {
        return repository
                .findByMerchantIdOrderByCreatedAtDesc(merchantId, PageRequest.of(page, size))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private SubscriptionEntity toEntity(Subscription s) {
        SubscriptionEntity e = new SubscriptionEntity();
        e.setId(s.getId());
        e.setMerchantId(s.getMerchantId());
        e.setCustomerName(s.getCustomerName());
        e.setCustomerEmail(s.getCustomerEmail());
        e.setCustomerDocument(s.getCustomerDocument());
        e.setAmountInCents(s.getAmountInCents());
        e.setMethod(s.getMethod());
        e.setCycle(s.getCycle());
        e.setStatus(s.getStatus());
        e.setNextDueDate(s.getNextDueDate());
        e.setDescription(s.getDescription());
        e.setPspProvider(s.getPspProvider());
        e.setPspSubscriptionId(s.getPspSubscriptionId());
        e.setCreatedAt(s.getCreatedAt());
        e.setUpdatedAt(s.getUpdatedAt());
        return e;
    }

    private Subscription toDomain(SubscriptionEntity e) {
        return new Subscription(
                e.getId(), e.getMerchantId(),
                e.getCustomerName(), e.getCustomerEmail(), e.getCustomerDocument(),
                e.getAmountInCents(), e.getMethod(), e.getCycle(), e.getStatus(),
                e.getNextDueDate(), e.getDescription(),
                e.getPspProvider(), e.getPspSubscriptionId(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
