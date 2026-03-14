package br.com.dompagamentos.application.ports.output;

import br.com.dompagamentos.domain.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Secondary Port — persistência de pagamentos.
 */
public interface PaymentRepositoryOutputPort {

    Payment save(Payment payment);

    Optional<Payment> findById(UUID id);

    Optional<Payment> findByPspPaymentId(String pspPaymentId);

    List<Payment> findByMerchantId(UUID merchantId, int page, int size);
}
