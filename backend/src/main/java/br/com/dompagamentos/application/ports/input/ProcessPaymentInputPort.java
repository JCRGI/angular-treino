package br.com.dompagamentos.application.ports.input;

import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.domain.model.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Primary Port — define o contrato para processar um pagamento.
 * Implementada pelo use case; chamada pelos adapters de entrada (REST, Camel).
 */
public interface ProcessPaymentInputPort {

    Payment execute(Command command);

    record Command(
            UUID merchantId,
            String customerName,
            String customerEmail,
            String customerDocument,
            BigDecimal amountInCents,
            PaymentMethod method,
            String description,
            LocalDate dueDate,
            Integer installments
    ) {}
}
