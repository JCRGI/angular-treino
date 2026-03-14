package br.com.dompagamentos.infrastructure.adapters.input.rest;

import br.com.dompagamentos.infrastructure.adapters.output.gateway.AsaasWebhookManagementAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de administração para gerenciar webhooks cadastrados no Asaas.
 *
 * Fluxo de setup inicial:
 *  1. POST /api/v1/admin/webhooks/asaas  → registra webhook, retorna authToken
 *  2. Salve o authToken como ASAAS_WEBHOOK_TOKEN nas variáveis de ambiente
 *  3. Reinicie a aplicação — o WebhookController passa a validar o token
 *
 * Para subcontas (merchants com walletId):
 *  - Inclua o campo walletId no body → webhook é registrado na conta do merchant
 */
@RestController
@RequestMapping("/api/v1/admin/webhooks/asaas")
@Tag(name = "Admin — Webhooks Asaas", description = "Gerenciamento de webhooks registrados no Asaas")
public class AsaasWebhookSetupController {

    private final AsaasWebhookManagementAdapter webhookAdapter;

    public AsaasWebhookSetupController(AsaasWebhookManagementAdapter webhookAdapter) {
        this.webhookAdapter = webhookAdapter;
    }

    @PostMapping
    @Operation(
        summary = "Registrar webhook no Asaas",
        description = """
            Cadastra um endpoint na sua conta Asaas para receber notificações de pagamento.

            **authToken:** retornado UMA única vez — salve imediatamente como `ASAAS_WEBHOOK_TOKEN`.

            **Eventos padrão:** todos os eventos de pagamento (PAYMENT_CONFIRMED, PAYMENT_RECEIVED, etc.).

            **walletId:** preencha para registrar na subconta de um merchant específico.
            """
    )
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        var result = webhookAdapter.register(
                request.url(),
                request.name(),
                request.email(),
                request.events(),
                request.sendType(),
                request.walletId()
        );

        return ResponseEntity.status(201).body(new RegisterResponse(
                result.id(),
                result.name(),
                result.url(),
                result.email(),
                result.enabled(),
                result.authToken(),
                result.sendType(),
                result.events(),
                result.authToken() != null
                        ? "Salve o authToken como variável de ambiente: ASAAS_WEBHOOK_TOKEN=" + result.authToken()
                        : null
        ));
    }

    @GetMapping
    @Operation(summary = "Listar webhooks cadastrados no Asaas")
    public ResponseEntity<List<AsaasWebhookManagementAdapter.WebhookInfo>> list(
            @RequestParam(required = false) String walletId) {
        return ResponseEntity.ok(webhookAdapter.list(walletId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover webhook do Asaas")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @RequestParam(required = false) String walletId) {
        webhookAdapter.delete(id, walletId);
        return ResponseEntity.noContent().build();
    }

    // ===== DTOs =====

    record RegisterRequest(

            @NotBlank(message = "url é obrigatória")
            @Pattern(regexp = "https?://.+", message = "url deve começar com http:// ou https://")
            String url,

            String name,
            String email,

            /**
             * Eventos a subscrever. Se não informado, usa todos os eventos de pagamento padrão.
             * Valores: PAYMENT_CONFIRMED, PAYMENT_RECEIVED, PAYMENT_OVERDUE, etc.
             */
            List<String> events,

            /**
             * SEQUENTIALLY (padrão): Asaas aguarda HTTP 2xx antes do próximo evento.
             * NON_SEQUENTIAL: disparo em paralelo.
             */
            String sendType,

            /**
             * ID da carteira do merchant (pspExternalId).
             * Preencha para registrar na subconta de um merchant específico.
             * Deixe vazio para registrar na conta principal da plataforma.
             */
            String walletId
    ) {}

    record RegisterResponse(
            String id,
            String name,
            String url,
            String email,
            boolean enabled,
            String authToken,    // SALVE AGORA — exibido apenas uma vez
            String sendType,
            List<String> events,
            String instruction   // mensagem de como salvar o authToken
    ) {}
}
