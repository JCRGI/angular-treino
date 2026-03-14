package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.RevokeApiKeyInputPort;
import br.com.dompagamentos.application.ports.output.MerchantApiKeyRepositoryOutputPort;
import br.com.dompagamentos.domain.exception.ResourceAccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class RevokeApiKeyUseCase implements RevokeApiKeyInputPort {

    private final MerchantApiKeyRepositoryOutputPort apiKeyRepository;

    public RevokeApiKeyUseCase(MerchantApiKeyRepositoryOutputPort apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public void execute(UUID apiKeyId, UUID merchantId) {
        var apiKey = apiKeyRepository.findByIdAndMerchantId(apiKeyId, merchantId)
                .orElseThrow(() -> new ResourceAccessDeniedException("api-key", apiKeyId));

        apiKey.revoke();
        apiKeyRepository.save(apiKey);
    }
}
