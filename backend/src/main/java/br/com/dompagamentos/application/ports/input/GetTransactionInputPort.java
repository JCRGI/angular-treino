package br.com.dompagamentos.application.ports.input;

import br.com.dompagamentos.domain.model.Payment;

import java.util.List;
import java.util.UUID;

/**
 * Primary Port — consulta de pagamentos.
 */
public interface GetTransactionInputPort {

    Payment findById(UUID paymentId);

    /** Busca por ID e valida que o pagamento pertence ao merchant informado (isolamento de tenant). */
    Payment findByIdAndMerchant(UUID paymentId, UUID merchantId);

    List<Payment> findByMerchant(UUID merchantId, int page, int size);

    Payment findByPspPaymentId(String pspPaymentId);
}
