package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.GenerateApiKeyInputPort;
import br.com.dompagamentos.application.ports.output.MerchantApiKeyRepositoryOutputPort;
import br.com.dompagamentos.application.ports.output.MerchantRepositoryOutputPort;
import br.com.dompagamentos.domain.exception.MerchantNotFoundException;
import br.com.dompagamentos.domain.model.MerchantApiKey;
import br.com.dompagamentos.infrastructure.security.ApiKeyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GenerateApiKeyUseCase implements GenerateApiKeyInputPort {

    private final MerchantRepositoryOutputPort merchantRepository;
    private final MerchantApiKeyRepositoryOutputPort apiKeyRepository;
    private final ApiKeyService apiKeyService;

    public GenerateApiKeyUseCase(MerchantRepositoryOutputPort merchantRepository,
                                 MerchantApiKeyRepositoryOutputPort apiKeyRepository,
                                 ApiKeyService apiKeyService) {
        this.merchantRepository = merchantRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyService = apiKeyService;
    }

    @Override
    public GenerateApiKeyResult execute(Command command) {
        merchantRepository.findById(command.merchantId())
                .orElseThrow(() -> new MerchantNotFoundException(command.merchantId()));

        String plaintext = apiKeyService.generatePlaintext();
        String hash      = apiKeyService.hash(plaintext);
        String prefix    = apiKeyService.visiblePrefix(plaintext);

        MerchantApiKey apiKey = MerchantApiKey.create(command.merchantId(), hash, prefix, command.description());
        MerchantApiKey saved  = apiKeyRepository.save(apiKey);

        return new GenerateApiKeyResult(plaintext, saved);
    }
}
