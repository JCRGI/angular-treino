package br.com.dompagamentos.infrastructure.adapters.output.gateway;

import br.com.dompagamentos.application.ports.output.PaymentGatewayOutputPort;
import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.domain.model.Subscription;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Base64;

/**
 * Adapter de saída — integra com a API REST do iugu.
 * Implementa PaymentGatewayOutputPort para o PSP iugu.
 *
 * Documentação: https://dev.iugu.com/reference
 */
@Component(IuguGatewayAdapter.BEAN_NAME)
public class IuguGatewayAdapter implements PaymentGatewayOutputPort {

    public static final String BEAN_NAME = "IUGU_GATEWAY";
    private static final Logger log = LoggerFactory.getLogger(IuguGatewayAdapter.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${psp.iugu.base-url}")
    private String baseUrl;

    @Value("${psp.iugu.api-key}")
    private String apiKey;

    @Value("${psp.iugu.account-id}")
    private String accountId;

    public IuguGatewayAdapter(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public GatewayResponse createPayment(Payment payment, Merchant merchant) {
        ObjectNode body = objectMapper.createObjectNode();

        // iugu usa centavos como string
        body.put("payer_name", payment.getCustomerName());
        body.put("payer_email", payment.getCustomerEmail());
        body.put("payer_cpf_cnpj", payment.getCustomerDocument());
        body.put("due_date", payment.getDueDate() != null
                ? payment.getDueDate().toString()
                : java.time.LocalDate.now().plusDays(3).toString());

        // Itens da fatura
        ArrayNode items = objectMapper.createArrayNode();
        ObjectNode item = objectMapper.createObjectNode();
        item.put("description", payment.getDescription() != null ? payment.getDescription() : "Cobrança Dom Pagamentos");
        item.put("quantity", 1);
        item.put("price_cents", payment.getAmountInCents().intValue());
        items.add(item);
        body.set("items", items);

        // Métodos de pagamento
        ArrayNode methods = objectMapper.createArrayNode();
        methods.add(mapMethod(payment));
        body.set("payable_with", methods);

        if (payment.getInstallments() != null && payment.getInstallments() > 1) {
            body.put("max_installments_value", payment.getInstallments());
        }

        try {
            JsonNode response = post("/invoices", body);
            return new GatewayResponse(
                    response.path("id").asText(),
                    response.path("secure_url").asText(null),
                    response.path("pix").path("qrcode").asText(null),
                    response.path("pix").path("qrcode_image_url").asText(null)
            );
        } catch (HttpClientErrorException e) {
            log.error("Erro ao criar fatura no iugu: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new GatewayException("Falha ao criar fatura no iugu: " + e.getMessage(), PspProvider.IUGU);
        }
    }

    @Override
    public String createSubAccount(Merchant merchant) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("name", merchant.getName());
        body.put("email", merchant.getEmail());
        body.put("cpf_cnpj", merchant.getDocument());
        body.put("account_type", "Merchant");

        try {
            JsonNode response = post("/marketplace/create_account", body);
            return response.path("account_id").asText();
        } catch (HttpClientErrorException e) {
            log.error("Erro ao criar subconta iugu: {}", e.getResponseBodyAsString());
            throw new GatewayException("Falha ao criar subconta no iugu", PspProvider.IUGU);
        }
    }

    @Override
    public void refundPayment(String pspPaymentId) {
        try {
            post("/invoices/" + pspPaymentId + "/refund", objectMapper.createObjectNode());
        } catch (HttpClientErrorException e) {
            log.error("Erro ao estornar fatura iugu {}: {}", pspPaymentId, e.getResponseBodyAsString());
            throw new GatewayException("Falha ao estornar pagamento no iugu", PspProvider.IUGU);
        }
    }

    @Override
    public SubscriptionGatewayResponse createSubscription(Subscription subscription, Merchant merchant) {
        // iugu não é usado para assinaturas nesta plataforma — Asaas é o PSP padrão para recorrência
        throw new GatewayException("Assinaturas não suportadas no iugu nesta plataforma", PspProvider.IUGU);
    }

    @Override
    public void cancelSubscription(String pspSubscriptionId) {
        throw new GatewayException("Assinaturas não suportadas no iugu nesta plataforma", PspProvider.IUGU);
    }

    @Override
    public java.math.BigDecimal getBalance() {
        throw new GatewayException("Consulta de saldo não disponível no iugu nesta plataforma", PspProvider.IUGU);
    }

    // ===== Helpers =====

    private JsonNode post(String path, Object body) {
        HttpEntity<Object> entity = new HttpEntity<>(body, headers());
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + path, HttpMethod.POST, entity, JsonNode.class);
        return response.getBody();
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // iugu usa Basic Auth com apiKey como usuário e senha vazia
        String credentials = Base64.getEncoder().encodeToString((apiKey + ":").getBytes());
        headers.set("Authorization", "Basic " + credentials);
        return headers;
    }

    private String mapMethod(Payment payment) {
        return switch (payment.getMethod()) {
            case CREDIT_CARD -> "credit_card";
            case DEBIT_CARD -> "debit_card";
            case PIX -> "pix";
            case BOLETO, CARNE -> "bank_slip";
        };
    }
}
