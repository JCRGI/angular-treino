package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.ListApiKeysInputPort;
import br.com.dompagamentos.application.ports.output.MerchantApiKeyRepositoryOutputPort;
import br.com.dompagamentos.domain.model.MerchantApiKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ListApiKeysUseCase implements ListApiKeysInputPort {

    private final MerchantApiKeyRepositoryOutputPort apiKeyRepository;

    public ListApiKeysUseCase(MerchantApiKeyRepositoryOutputPort apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public List<MerchantApiKey> execute(UUID merchantId) {
        return apiKeyRepository.findByMerchantId(merchantId);
    }
}
