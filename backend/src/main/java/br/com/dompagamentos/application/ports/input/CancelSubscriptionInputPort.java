package br.com.dompagamentos.application.ports.input;

import java.util.UUID;

public interface CancelSubscriptionInputPort {

    void execute(UUID subscriptionId);
}
