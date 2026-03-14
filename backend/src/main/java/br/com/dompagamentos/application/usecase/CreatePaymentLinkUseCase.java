package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.CreatePaymentLinkInputPort;
import br.com.dompagamentos.application.ports.output.MerchantRepositoryOutputPort;
import br.com.dompagamentos.application.ports.output.PaymentLinkGatewayOutputPort;
import br.com.dompagamentos.domain.exception.MerchantNotFoundException;
import br.com.dompagamentos.domain.model.Merchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Use Case — gera um payment link para o merchant autenticado.
 *
 * Fluxo:
 *  1. Busca o merchant e valida que está ativo
 *  2. Delega a criação do link ao gateway (Asaas /paymentLinks)
 *  3. Retorna a URL pronta para compartilhar
 */
@Service
public class CreatePaymentLinkUseCase implements CreatePaymentLinkInputPort {

    private static final Logger log = LoggerFactory.getLogger(CreatePaymentLinkUseCase.class);

    private final MerchantRepositoryOutputPort merchantRepository;
    private final PaymentLinkGatewayOutputPort paymentLinkGateway;

    public CreatePaymentLinkUseCase(MerchantRepositoryOutputPort merchantRepository,
                                    PaymentLinkGatewayOutputPort paymentLinkGateway) {
        this.merchantRepository = merchantRepository;
        this.paymentLinkGateway = paymentLinkGateway;
    }

    @Override
    public Result execute(Command command) {
        Merchant merchant = merchantRepository.findById(command.merchantId())
                .filter(Merchant::isActive)
                .orElseThrow(() -> new MerchantNotFoundException(command.merchantId()));

        PaymentLinkGatewayOutputPort.PaymentLinkResult gatewayResult = paymentLinkGateway.createPaymentLink(
                merchant.getPspExternalId(),
                new PaymentLinkGatewayOutputPort.Command(
                        command.name(),
                        command.amountInCents(),
                        command.description(),
                        command.expiresAt()
                )
        );

        log.info("Payment link gerado para merchant {}: {}", merchant.getId(), gatewayResult.url());

        return new Result(
                gatewayResult.pspLinkId(),
                gatewayResult.url(),
                command.name(),
                command.amountInCents(),
                command.description(),
                command.expiresAt()
        );
    }
}
