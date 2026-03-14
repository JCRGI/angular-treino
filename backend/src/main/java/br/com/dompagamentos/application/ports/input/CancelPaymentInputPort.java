package br.com.dompagamentos.application.ports.input;

import java.util.UUID;

public interface CancelPaymentInputPort {

    void execute(UUID paymentId);
}
