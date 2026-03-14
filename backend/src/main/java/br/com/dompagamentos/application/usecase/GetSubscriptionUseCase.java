package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.GetSubscriptionInputPort;
import br.com.dompagamentos.application.ports.output.SubscriptionRepositoryOutputPort;
import br.com.dompagamentos.domain.exception.SubscriptionNotFoundException;
import br.com.dompagamentos.domain.model.Subscription;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetSubscriptionUseCase implements GetSubscriptionInputPort {

    private final SubscriptionRepositoryOutputPort subscriptionRepository;

    public GetSubscriptionUseCase(SubscriptionRepositoryOutputPort subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Subscription findById(UUID id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException(id));
    }

    @Override
    public List<Subscription> findByMerchant(UUID merchantId, int page, int size) {
        return subscriptionRepository.findByMerchantId(merchantId, page, size);
    }
}
