package br.com.dompagamentos.infrastructure.adapters.input.rest;

import br.com.dompagamentos.application.ports.input.GenerateApiKeyInputPort;
import br.com.dompagamentos.application.ports.input.ListApiKeysInputPort;
import br.com.dompagamentos.application.ports.input.RevokeApiKeyInputPort;
import br.com.dompagamentos.domain.model.MerchantApiKey;
import br.com.dompagamentos.infrastructure.security.MerchantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Gerenciamento de API Keys.
 * Requer autenticação via JWT (dashboard).
 *
 * A chave em texto plano é retornada UMA única vez na criação.
 * Guarde-a imediatamente — não é possível recuperá-la depois.
 */
@RestController
@RequestMapping("/api/v1/api-keys")
@Tag(name = "API Keys", description = "Geração e revogação de chaves para integração B2B")
public class ApiKeyController {

    private final GenerateApiKeyInputPort generateApiKey;
    private final RevokeApiKeyInputPort revokeApiKey;
    private final ListApiKeysInputPort listApiKeys;

    public ApiKeyController(GenerateApiKeyInputPort generateApiKey,
                            RevokeApiKeyInputPort revokeApiKey,
                            ListApiKeysInputPort listApiKeys) {
        this.generateApiKey = generateApiKey;
        this.revokeApiKey = revokeApiKey;
        this.listApiKeys = listApiKeys;
    }

    @PostMapping
    @Operation(summary = "Gerar API Key",
               description = "Gera uma nova API Key. O valor em texto plano é exibido UMA única vez.")
    public ResponseEntity<GenerateApiKeyResponse> generate(@Valid @RequestBody GenerateApiKeyRequest request) {
        UUID merchantId = MerchantContext.currentId();
        var result = generateApiKey.execute(
                new GenerateApiKeyInputPort.Command(merchantId, request.description())
        );
        return ResponseEntity.status(201).body(new GenerateApiKeyResponse(
                result.apiKey().getId(),
                result.plaintextKey(),   // exibido só aqui
                result.apiKey().getVisiblePrefix(),
                result.apiKey().getDescription(),
                result.apiKey().getCreatedAt()
        ));
    }

    @GetMapping
    @Operation(summary = "Listar API Keys",
               description = "Lista as chaves do merchant autenticado. Não retorna o valor real da chave.")
    public ResponseEntity<List<ApiKeyResponse>> list() {
        UUID merchantId = MerchantContext.currentId();
        List<ApiKeyResponse> keys = listApiKeys.execute(merchantId)
                .stream()
                .map(ApiKeyResponse::from)
                .toList();
        return ResponseEntity.ok(keys);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Revogar API Key", description = "Desativa a chave permanentemente.")
    public ResponseEntity<Void> revoke(@PathVariable UUID id) {
        UUID merchantId = MerchantContext.currentId();
        revokeApiKey.execute(id, merchantId);
        return ResponseEntity.noContent().build();
    }

    // ===== Records =====

    record GenerateApiKeyRequest(
            @Size(max = 200) String description
    ) {}

    /** Contém o plaintextKey — exibido APENAS na resposta de criação. */
    record GenerateApiKeyResponse(
            UUID id,
            String key,           // valor completo — exibir e armazenar uma vez
            String visiblePrefix, // para identificar depois
            String description,
            LocalDateTime createdAt
    ) {}

    record ApiKeyResponse(
            UUID id,
            String visiblePrefix,
            String description,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime lastUsedAt
    ) {
        static ApiKeyResponse from(MerchantApiKey k) {
            return new ApiKeyResponse(k.getId(), k.getVisiblePrefix(), k.getDescription(),
                    k.isActive(), k.getCreatedAt(), k.getLastUsedAt());
        }
    }
}
