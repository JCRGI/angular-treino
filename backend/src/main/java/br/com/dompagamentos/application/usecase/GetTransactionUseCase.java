package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.GetTransactionInputPort;
import br.com.dompagamentos.application.ports.output.PaymentRepositoryOutputPort;
import br.com.dompagamentos.domain.exception.PaymentNotFoundException;
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
