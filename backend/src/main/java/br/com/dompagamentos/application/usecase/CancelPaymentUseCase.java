package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.CancelPaymentInputPort;
import br.com.dompagamentos.application.ports.output.PaymentRepositoryOutputPort;
import br.com.dompagamentos.domain.exception.PaymentNotFoundException;
import br.com.dompagamentos.domain.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CancelPaymentUseCase implements CancelPaymentInputPort {

    private static final Logger log = LoggerFactory.getLogger(CancelPaymentUseCase.class);

    private final PaymentRepositoryOutputPort paymentRepository;

    public CancelPaymentUseCase(PaymentRepositoryOutputPort paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void execute(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        payment.cancel();
        paymentRepository.save(payment);
        log.info("Pagamento {} cancelado", paymentId);
    }
}
