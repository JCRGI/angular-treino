package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.CreateSubscriptionInputPort;
import br.com.dompagamentos.application.ports.output.MerchantRepositoryOutputPort;
import br.com.dompagamentos.application.ports.output.PaymentGatewayOutputPort;
import br.com.dompagamentos.application.ports.output.SubscriptionRepositoryOutputPort;
import br.com.dompagamentos.domain.exception.MerchantNotFoundException;
import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.domain.model.Subscription;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class CreateSubscriptionUseCase implements CreateSubscriptionInputPort {

    private static final Logger log = LoggerFactory.getLogger(CreateSubscriptionUseCase.class);

    private final MerchantRepositoryOutputPort merchantRepository;
    private final SubscriptionRepositoryOutputPort subscriptionRepository;
    private final Map<PspProvider, PaymentGatewayOutputPort> gateways;

    public CreateSubscriptionUseCase(MerchantRepositoryOutputPort merchantRepository,
                                     SubscriptionRepositoryOutputPort subscriptionRepository,
                                     Map<PspProvider, PaymentGatewayOutputPort> gateways) {
        this.merchantRepository = merchantRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.gateways = gateways;
    }

    @Override
    public Subscription execute(Command command) {
        Merchant merchant = merchantRepository.findById(command.merchantId())
                .filter(Merchant::isActive)
                .orElseThrow(() -> new MerchantNotFoundException(command.merchantId()));

        Subscription subscription = Subscription.create(
                merchant.getId(),
                command.customerName(),
                command.customerEmail(),
                command.customerDocument(),
                command.amountInCents(),
                command.method(),
                command.cycle(),
                command.nextDueDate(),
                command.description()
        );

        // Assinaturas sempre vão para Asaas por enquanto
        PaymentGatewayOutputPort gateway = gateways.get(PspProvider.ASAAS);
        PaymentGatewayOutputPort.SubscriptionGatewayResponse response =
                gateway.createSubscription(subscription, merchant);

        subscription.confirmWithPspData(response.pspSubscriptionId(), response.nextDueDate());

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("Assinatura {} criada para merchant {}", saved.getId(), merchant.getId());
        return saved;
    }
}
