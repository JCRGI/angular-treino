package br.com.dompagamentos.infrastructure.adapters.input.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Recebe notificações (webhooks) dos PSPs e encaminha para as rotas Camel.
 * Validação de assinatura feita antes do processamento para segurança.
 */
@RestController
@RequestMapping("/api/v1/webhooks")
@Tag(name = "Webhooks", description = "Receptor de notificações dos PSPs")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final ProducerTemplate producerTemplate;

    @Value("${psp.asaas.webhook-token:}")
    private String asaasWebhookToken;

    @Value("${psp.ebanx.webhook-secret:}")
    private String ebanxWebhookSecret;

    public WebhookController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @PostMapping("/asaas")
    @Operation(summary = "Webhook Asaas", description = "Endpoint para notificações de status de pagamento do Asaas.")
    public ResponseEntity<Void> asaasWebhook(
            @RequestHeader(value = "asaas-access-token", required = false) String token,
            @RequestBody JsonNode payload) {

        if (asaasWebhookToken != null && !asaasWebhookToken.isBlank()
                && !asaasWebhookToken.equals(token)) {
            log.warn("Webhook Asaas recebido com token inválido");
            return ResponseEntity.status(401).build();
        }

        log.info("Webhook Asaas recebido: event={}", payload.path("event").asText());
        producerTemplate.sendBody("direct:asaas-webhook", payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/iugu")
    @Operation(summary = "Webhook iugu")
    public ResponseEntity<Void> iuguWebhook(@RequestBody JsonNode payload) {
        log.info("Webhook iugu recebido: event={}", payload.path("event").asText());
        producerTemplate.sendBody("direct:iugu-webhook", payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ebanx")
    @Operation(summary = "Webhook EBANX", description = "Endpoint para notificações de status de pagamento do EBANX.")
    public ResponseEntity<Void> ebanxWebhook(
            @RequestHeader(value = "x-ebanx-signature", required = false) String signature,
            @RequestBody JsonNode payload) {

        if (ebanxWebhookSecret != null && !ebanxWebhookSecret.isBlank()
                && !ebanxWebhookSecret.equals(signature)) {
            log.warn("Webhook EBANX recebido com assinatura inválida");
            return ResponseEntity.status(401).build();
        }

        log.info("Webhook EBANX recebido: operation_type={}", payload.path("operation_type").asText());
        producerTemplate.sendBody("direct:ebanx-webhook", payload);
        return ResponseEntity.ok().build();
    }
}
