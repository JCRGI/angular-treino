package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.ProcessPaymentInputPort;
import br.com.dompagamentos.application.ports.output.MerchantRepositoryOutputPort;
import br.com.dompagamentos.application.ports.output.PaymentGatewayOutputPort;
import br.com.dompagamentos.application.ports.output.PaymentRepositoryOutputPort;
import br.com.dompagamentos.domain.exception.MerchantNotFoundException;
import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import br.com.dompagamentos.domain.service.PspRoutingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Use Case — orquestra o processamento de um pagamento.
 *
 * Fluxo:
 *  1. Busca o merchant e valida que está ativo
 *  2. Cria a entidade Payment no domínio
 *  3. Decide qual PSP usar (PspRoutingService)
 *  4. Chama o gateway correto via output port
 *  5. Atualiza o payment com os dados do PSP
 *  6. Persiste e retorna
 *
 * Nota: A decisão de PSP está no domínio; a execução HTTP está na infra.
 */
@Service
@Transactional
public class ProcessPaymentUseCase implements ProcessPaymentInputPort {

    private static final Logger log = LoggerFactory.getLogger(ProcessPaymentUseCase.class);

    private final MerchantRepositoryOutputPort merchantRepository;
    private final PaymentRepositoryOutputPort paymentRepository;
    private final PspRoutingService pspRoutingService;
    private final Map<PspProvider, PaymentGatewayOutputPort> gateways;

    public ProcessPaymentUseCase(
            MerchantRepositoryOutputPort merchantRepository,
            PaymentRepositoryOutputPort paymentRepository,
            PspRoutingService pspRoutingService,
            Map<PspProvider, PaymentGatewayOutputPort> gateways) {
        this.merchantRepository = merchantRepository;
        this.paymentRepository = paymentRepository;
        this.pspRoutingService = pspRoutingService;
        this.gateways = gateways;
    }

    @Override
    public Payment execute(Command command) {
        // 1. Busca merchant
        Merchant merchant = merchantRepository.findById(command.merchantId())
                .filter(Merchant::isActive)
                .orElseThrow(() -> new MerchantNotFoundException(command.merchantId()));

        // 2. Cria a entidade de domínio
        Payment payment = Payment.create(
                merchant.getId(),
                command.customerName(),
                command.customerEmail(),
                command.customerDocument(),
                command.amountInCents(),
                command.method(),
                command.description(),
                command.dueDate()
        );

        // 3. Decide o PSP
        PspProvider provider = pspRoutingService.route(merchant);
        log.info("Roteando pagamento {} para PSP: {}", payment.getId(), provider);

        // 4. Inicia o processamento
        payment.startProcessing(provider);

        // 5. Chama o gateway
        PaymentGatewayOutputPort gateway = gateways.get(provider);
        PaymentGatewayOutputPort.GatewayResponse response = gateway.createPayment(payment, merchant);

        // 6. Confirma com dados do PSP
        payment.confirmWithPspData(
                response.pspPaymentId(),
                response.paymentUrl(),
                response.pixQrCode()
        );

        // 7. Persiste e retorna
        Payment saved = paymentRepository.save(payment);
        log.info("Pagamento {} criado com sucesso via {}", saved.getId(), provider);
        return saved;
    }
}
