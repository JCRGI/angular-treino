package br.com.dompagamentos.application.ports.input;

import java.util.UUID;

public interface RefundPaymentInputPort {

    void execute(UUID paymentId);
}
