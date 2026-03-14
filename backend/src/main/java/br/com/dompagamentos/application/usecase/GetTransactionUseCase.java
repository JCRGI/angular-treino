package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.GetTransactionInputPort;
import br.com.dompagamentos.application.ports.output.PaymentRepositoryOutputPort;
import br.com.dompagamentos.domain.exception.PaymentNotFoundException;
import br.com.dompagamentos.domain.exception.ResourceAccessDeniedException;
import br.com.dompagamentos.domain.model.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetTransactionUseCase implements GetTransactionInputPort {

    private final PaymentRepositoryOutputPort paymentRepository;

    public GetTransactionUseCase(PaymentRepositoryOutputPort paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment findById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }

    /**
     * Busca o pagamento e valida que pertence ao merchantId informado.
     * Garante isolamento de tenant sem precisar de autenticação.
     */
    @Override
    public Payment findByIdAndMerchant(UUID paymentId, UUID merchantId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (!payment.getMerchantId().equals(merchantId)) {
            throw new ResourceAccessDeniedException("pagamento", paymentId);
        }
        return payment;
    }

    @Override
    public List<Payment> findByMerchant(UUID merchantId, int page, int size) {
        return paymentRepository.findByMerchantId(merchantId, page, size);
    }

    @Override
    public Payment findByPspPaymentId(String pspPaymentId) {
        return paymentRepository.findByPspPaymentId(pspPaymentId)
                .orElseThrow(() -> new PaymentNotFoundException(pspPaymentId));
    }
}
