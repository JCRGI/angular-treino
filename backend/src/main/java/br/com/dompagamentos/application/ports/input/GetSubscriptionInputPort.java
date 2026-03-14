package br.com.dompagamentos.application.ports.input;

import br.com.dompagamentos.domain.model.Subscription;

import java.util.List;
import java.util.UUID;

public interface GetSubscriptionInputPort {

    Subscription findById(UUID id);

    List<Subscription> findByMerchant(UUID merchantId, int page, int size);
}
