package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.CancelSubscriptionInputPort;
import br.com.dompagamentos.application.ports.output.PaymentGatewayOutputPort;
import br.com.dompagamentos.application.ports.output.SubscriptionRepositoryOutputPort;
import br.com.dompagamentos.domain.exception.SubscriptionNotFoundException;
import br.com.dompagamentos.domain.model.Subscription;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class CancelSubscriptionUseCase implements CancelSubscriptionInputPort {

    private static final Logger log = LoggerFactory.getLogger(CancelSubscriptionUseCase.class);

    private final SubscriptionRepositoryOutputPort subscriptionRepository;
    private final Map<PspProvider, PaymentGatewayOutputPort> gateways;

    public CancelSubscriptionUseCase(SubscriptionRepositoryOutputPort subscriptionRepository,
                                     Map<PspProvider, PaymentGatewayOutputPort> gateways) {
        this.subscriptionRepository = subscriptionRepository;
        this.gateways = gateways;
    }

    @Override
    public void execute(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));

        if (subscription.getPspSubscriptionId() != null) {
            PaymentGatewayOutputPort gateway = gateways.get(subscription.getPspProvider());
            gateway.cancelSubscription(subscription.getPspSubscriptionId());
        }

        subscription.cancel();
        subscriptionRepository.save(subscription);
        log.info("Assinatura {} cancelada", subscriptionId);
    }
}
