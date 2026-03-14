package br.com.dompagamentos.application.ports.input;

import br.com.dompagamentos.domain.model.MerchantApiKey;

import java.util.List;
import java.util.UUID;

public interface ListApiKeysInputPort {

    List<MerchantApiKey> execute(UUID merchantId);
}
