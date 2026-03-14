package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.RefundPaymentInputPort;
import br.com.dompagamentos.application.ports.output.PaymentGatewayOutputPort;
import br.com.dompagamentos.application.ports.output.PaymentRepositoryOutputPort;
import br.com.dompagamentos.domain.exception.PaymentNotFoundException;
import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class RefundPaymentUseCase implements RefundPaymentInputPort {

    private static final Logger log = LoggerFactory.getLogger(RefundPaymentUseCase.class);

    private final PaymentRepositoryOutputPort paymentRepository;
    private final Map<PspProvider, PaymentGatewayOutputPort> gateways;

    public RefundPaymentUseCase(PaymentRepositoryOutputPort paymentRepository,
                                Map<PspProvider, PaymentGatewayOutputPort> gateways) {
        this.paymentRepository = paymentRepository;
        this.gateways = gateways;
    }

    @Override
    public void execute(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        // Valida transição de estado no domínio
        payment.requestRefund();

        // Chama o PSP para estornar
        if (payment.getPspPaymentId() != null) {
            PaymentGatewayOutputPort gateway = gateways.get(payment.getPspProvider());
            gateway.refundPayment(payment.getPspPaymentId());
        }

        paymentRepository.save(payment);
        log.info("Estorno solicitado para pagamento {}", paymentId);
    }
}
