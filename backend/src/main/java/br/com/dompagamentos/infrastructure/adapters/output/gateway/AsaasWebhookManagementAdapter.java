package br.com.dompagamentos.infrastructure.adapters.output.gateway;

import br.com.dompagamentos.domain.model.enums.PspProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia registros de webhook na API Asaas.
 * Documentação: https://docs.asaas.com/docs/criar-novo-webhook-pela-api
 *
 * Limite: 10 webhooks por conta (principal ou subconta).
 *
 * sendType SEQUENTIALLY → Asaas espera confirmação (HTTP 2xx) antes do próximo evento.
 * sendType NON_SEQUENTIAL → disparo em paralelo (menos garantias de ordem).
 */
@Component
public class AsaasWebhookManagementAdapter {

    private static final Logger log = LoggerFactory.getLogger(AsaasWebhookManagementAdapter.class);

    /**
     * Eventos de pagamento que a plataforma processa.
     * Espelho do mapeamento em WebhookProcessingRoute.
     */
    public static final List<String> PAYMENT_EVENTS = List.of(
            "PAYMENT_CREATED",
            "PAYMENT_AUTHORIZED",
            "PAYMENT_UPDATED",
            "PAYMENT_CONFIRMED",
            "PAYMENT_RECEIVED",
            "PAYMENT_OVERDUE",
            "PAYMENT_DELETED",
            "PAYMENT_RESTORED",
            "PAYMENT_REFUNDED",
            "PAYMENT_PARTIALLY_REFUNDED",
            "PAYMENT_CHARGEBACK_REQUESTED",
            "PAYMENT_CHARGEBACK_DISPUTE",
            "PAYMENT_AWAITING_CHARGEBACK_REVERSAL",
            "PAYMENT_DUNNING_REQUESTED",
            "PAYMENT_DUNNING_RECEIVED",
            "PAYMENT_AWAITING_RISK_ANALYSIS",
            "PAYMENT_APPROVED_BY_RISK_ANALYSIS",
            "PAYMENT_REPROVED_BY_RISK_ANALYSIS"
    );

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${psp.asaas.base-url}")
    private String baseUrl;

    @Value("${psp.asaas.api-key}")
    private String apiKey;

    public AsaasWebhookManagementAdapter(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // ===== Criar webhook =====

    /**
     * Registra um webhook na conta principal do Asaas.
     *
     * @param url       URL que receberá os eventos (ex: https://api.seusite.com.br/api/v1/webhooks/asaas)
     * @param name      Nome identificador (ex: "Dom Pagamentos - Produção")
     * @param email     E-mail de contato para notificações de erro
     * @param events    Lista de eventos; se null usa PAYMENT_EVENTS padrão
     * @param sendType  "SEQUENTIALLY" ou "NON_SEQUENTIAL"; padrão: SEQUENTIALLY
     */
    public WebhookRegistrationResult register(String url, String name, String email,
                                               List<String> events, String sendType) {
        return register(url, name, email, events, sendType, null);
    }

    /**
     * Registra webhook em uma subconta específica (merchant com walletId).
     *
     * @param walletId  ID da carteira do merchant no Asaas (pspExternalId)
     */
    public WebhookRegistrationResult register(String url, String name, String email,
                                               List<String> events, String sendType,
                                               String walletId) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("name", name != null ? name : "Dom Pagamentos Webhook");
        body.put("url", url);
        if (email != null) body.put("email", email);
        body.put("enabled", true);
        body.put("interrupted", false);
        body.put("sendType", sendType != null ? sendType : "SEQUENTIALLY");

        var eventsArray = objectMapper.createArrayNode();
        (events != null ? events : PAYMENT_EVENTS).forEach(eventsArray::add);
        body.set("events", eventsArray);

        try {
            JsonNode response = post("/webhooks", body, walletId);

            String authToken = response.path("authToken").asText(null);
            log.info("Webhook Asaas registrado: id={}, url={}, walletId={}",
                    response.path("id").asText(), url, walletId);

            if (authToken != null) {
                log.info("=== IMPORTANTE: salve o authToken como variável de ambiente " +
                        "ASAAS_WEBHOOK_TOKEN={}  (exibido apenas uma vez) ===", authToken);
            }

            return parseResult(response);

        } catch (HttpClientErrorException e) {
            log.error("Erro ao registrar webhook Asaas: {} — {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new GatewayException("Falha ao registrar webhook no Asaas: " + e.getMessage(), PspProvider.ASAAS);
        }
    }

    // ===== Listar webhooks =====

    public List<WebhookInfo> list() {
        return list(null);
    }

    public List<WebhookInfo> list(String walletId) {
        try {
            JsonNode response = get("/webhooks", walletId);
            List<WebhookInfo> result = new ArrayList<>();
            response.path("data").forEach(node -> result.add(parseInfo(node)));
            return result;
        } catch (HttpClientErrorException e) {
            log.error("Erro ao listar webhooks Asaas: {}", e.getResponseBodyAsString());
            throw new GatewayException("Falha ao listar webhooks no Asaas", PspProvider.ASAAS);
        }
    }

    // ===== Deletar webhook =====

    public void delete(String webhookId) {
        delete(webhookId, null);
    }

    public void delete(String webhookId, String walletId) {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(headers(walletId));
            restTemplate.exchange(baseUrl + "/webhooks/" + webhookId,
                    HttpMethod.DELETE, entity, Void.class);
            log.info("Webhook Asaas removido: id={}", webhookId);
        } catch (HttpClientErrorException e) {
            log.error("Erro ao deletar webhook Asaas {}: {}", webhookId, e.getResponseBodyAsString());
            throw new GatewayException("Falha ao remover webhook do Asaas", PspProvider.ASAAS);
        }
    }

    // ===== Helpers =====

    private JsonNode post(String path, Object body, String walletId) {
        HttpEntity<Object> entity = new HttpEntity<>(body, headers(walletId));
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + path, HttpMethod.POST, entity, JsonNode.class);
        return response.getBody();
    }

    private JsonNode get(String path, String walletId) {
        HttpEntity<Void> entity = new HttpEntity<>(headers(walletId));
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + path, HttpMethod.GET, entity, JsonNode.class);
        return response.getBody();
    }

    private HttpHeaders headers(String walletId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("access_token", apiKey);
        if (walletId != null && !walletId.isBlank()) {
            headers.set("asaas-wallet-id", walletId);
        }
        return headers;
    }

    private WebhookRegistrationResult parseResult(JsonNode node) {
        List<String> events = new ArrayList<>();
        node.path("events").forEach(e -> events.add(e.asText()));
        return new WebhookRegistrationResult(
                node.path("id").asText(),
                node.path("name").asText(),
                node.path("url").asText(),
                node.path("email").asText(null),
                node.path("enabled").asBoolean(true),
                node.path("authToken").asText(null),
                node.path("sendType").asText(),
                events
        );
    }

    private WebhookInfo parseInfo(JsonNode node) {
        List<String> events = new ArrayList<>();
        node.path("events").forEach(e -> events.add(e.asText()));
        return new WebhookInfo(
                node.path("id").asText(),
                node.path("name").asText(),
                node.path("url").asText(),
                node.path("enabled").asBoolean(true),
                node.path("interrupted").asBoolean(false),
                node.path("sendType").asText(),
                events
        );
    }

    // ===== Records de resultado =====

    /**
     * Resultado do registro de webhook.
     * O authToken é retornado UMA única vez — salve como ASAAS_WEBHOOK_TOKEN.
     */
    public record WebhookRegistrationResult(
            String id,
            String name,
            String url,
            String email,
            boolean enabled,
            String authToken,   // salvar como ASAAS_WEBHOOK_TOKEN imediatamente!
            String sendType,
            List<String> events
    ) {}

    public record WebhookInfo(
            String id,
            String name,
            String url,
            boolean enabled,
            boolean interrupted,
            String sendType,
            List<String> events
    ) {}
}
