package br.com.dompagamentos.application.ports.output;

import br.com.dompagamentos.domain.model.Subscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepositoryOutputPort {

    Subscription save(Subscription subscription);

    Optional<Subscription> findById(UUID id);

    List<Subscription> findByMerchantId(UUID merchantId, int page, int size);
}
