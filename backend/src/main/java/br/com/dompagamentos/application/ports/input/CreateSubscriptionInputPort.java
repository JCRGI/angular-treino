package br.com.dompagamentos.application.ports.input;

import br.com.dompagamentos.domain.model.Subscription;
import br.com.dompagamentos.domain.model.enums.PaymentMethod;
import br.com.dompagamentos.domain.model.enums.SubscriptionCycle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface CreateSubscriptionInputPort {

    Subscription execute(Command command);

    record Command(
            UUID merchantId,
            String customerName,
            String customerEmail,
            String customerDocument,
            BigDecimal amountInCents,
            PaymentMethod method,
            SubscriptionCycle cycle,
            LocalDate nextDueDate,
            String description
    ) {}
}
