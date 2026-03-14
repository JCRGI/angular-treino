package br.com.dompagamentos.application.ports.output;

import br.com.dompagamentos.domain.model.MerchantApiKey;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantApiKeyRepositoryOutputPort {

    MerchantApiKey save(MerchantApiKey apiKey);

    Optional<MerchantApiKey> findByKeyHash(String keyHash);

    Optional<MerchantApiKey> findByIdAndMerchantId(UUID id, UUID merchantId);

    List<MerchantApiKey> findByMerchantId(UUID merchantId);
}
