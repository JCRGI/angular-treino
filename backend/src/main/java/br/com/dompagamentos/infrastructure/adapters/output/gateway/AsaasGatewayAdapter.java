package br.com.dompagamentos.infrastructure.adapters.output.gateway;

import br.com.dompagamentos.application.ports.output.PaymentGatewayOutputPort;
import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.domain.model.Subscription;
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

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Adapter de saída — integra com a API REST do Asaas v3.
 * Documentação: https://docs.asaas.com/reference/comece-por-aqui
 *
 * Fluxo de cobrança:
 *  1. createOrFindCustomer() — garante que o cliente existe no Asaas
 *  2. createPayment()        — cria a cobrança referenciando o cliente
 *  3. Para PIX: o QR code vem no campo pix.payload da resposta
 */
@Component(AsaasGatewayAdapter.BEAN_NAME)
public class AsaasGatewayAdapter implements PaymentGatewayOutputPort {

    public static final String BEAN_NAME = "ASAAS_GATEWAY";
    private static final Logger log = LoggerFactory.getLogger(AsaasGatewayAdapter.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${psp.asaas.base-url}")
    private String baseUrl;

    @Value("${psp.asaas.api-key}")
    private String apiKey;

    public AsaasGatewayAdapter(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // ===== Cobrança =====

    @Override
    public GatewayResponse createPayment(Payment payment, Merchant merchant) {
        // 1. Garante que o cliente existe no Asaas e obtém o ID externo
        String asaasCustomerId = createOrFindCustomer(
                payment.getCustomerName(),
                payment.getCustomerEmail(),
                payment.getCustomerDocument()
        );

        // 2. Monta o body da cobrança
        BigDecimal valueInReais = payment.getAmountInCents()
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        ObjectNode body = objectMapper.createObjectNode();
        body.put("customer", asaasCustomerId);
        body.put("billingType", mapBillingType(payment));
        body.put("value", valueInReais.doubleValue());
        body.put("dueDate", payment.getDueDate() != null
                ? payment.getDueDate().toString()
                : java.time.LocalDate.now().plusDays(3).toString());
        body.put("externalReference", payment.getId().toString());

        if (payment.getDescription() != null) {
            body.put("description", payment.getDescription());
        }

        if (payment.getInstallments() != null && payment.getInstallments() > 1) {
            body.put("installmentCount", payment.getInstallments());
            body.put("installmentValue",
                    valueInReais.divide(BigDecimal.valueOf(payment.getInstallments()), 2, RoundingMode.HALF_UP)
                            .doubleValue());
        }

        // Wallet do merchant (subconta Asaas), se disponível
        if (merchant.getPspExternalId() != null) {
            body.put("split", objectMapper.createArrayNode()
                    .add(objectMapper.createObjectNode()
                            .put("walletId", merchant.getPspExternalId())
                            .put("percentualValue", 100.0)));
        }

        try {
            JsonNode response = post("/charges", body);
            String pspId = response.path("id").asText();

            // URL de pagamento varia por método
            String paymentUrl = resolvePaymentUrl(response, payment);

            // PIX: QR code vem em pix.payload
            String pixPayload = response.path("pix").path("payload").asText(null);
            String pixBase64  = response.path("pix").path("encodedImage").asText(null);

            log.info("Cobrança criada no Asaas: id={}, customer={}", pspId, asaasCustomerId);
            return new GatewayResponse(pspId, paymentUrl, pixPayload, pixBase64);

        } catch (HttpClientErrorException e) {
            log.error("Erro ao criar cobrança no Asaas: {} — {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new GatewayException("Falha ao criar cobrança no Asaas: " + e.getMessage(), PspProvider.ASAAS);
        }
    }

    // ===== Subconta (white-label) =====

    @Override
    public String createSubAccount(Merchant merchant) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("name", merchant.getName());
        body.put("email", merchant.getEmail());
        body.put("cpfCnpj", merchant.getDocument());
        if (merchant.getPhone() != null) {
            body.put("mobilePhone", merchant.getPhone());
        }
        body.put("companyType", "MEI");
        body.put("incomeValue", 1000.0);

        try {
            JsonNode response = post("/accounts", body);
            String walletId = response.path("walletId").asText();
            log.info("Subconta Asaas criada: walletId={}", walletId);
            return walletId;
        } catch (HttpClientErrorException e) {
            log.error("Erro ao criar subconta Asaas: {}", e.getResponseBodyAsString());
            throw new GatewayException("Falha ao criar subconta no Asaas", PspProvider.ASAAS);
        }
    }

    // ===== Estorno =====

    @Override
    public void refundPayment(String pspPaymentId) {
        try {
            post("/charges/" + pspPaymentId + "/refund", objectMapper.createObjectNode());
            log.info("Estorno solicitado no Asaas para cobrança: {}", pspPaymentId);
        } catch (HttpClientErrorException e) {
            log.error("Erro ao estornar cobrança Asaas {}: {}", pspPaymentId, e.getResponseBodyAsString());
            throw new GatewayException("Falha ao estornar pagamento no Asaas", PspProvider.ASAAS);
        }
    }

    // ===== Assinatura =====

    @Override
    public SubscriptionGatewayResponse createSubscription(Subscription subscription, Merchant merchant) {
        String asaasCustomerId = createOrFindCustomer(
                subscription.getCustomerName(),
                subscription.getCustomerEmail(),
                subscription.getCustomerDocument()
        );

        BigDecimal valueInReais = subscription.getAmountInCents()
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        ObjectNode body = objectMapper.createObjectNode();
        body.put("customer", asaasCustomerId);
        body.put("billingType", subscription.getMethod().name());
        body.put("value", valueInReais.doubleValue());
        body.put("nextDueDate", subscription.getNextDueDate().toString());
        body.put("cycle", subscription.getCycle().name());
        body.put("externalReference", subscription.getId().toString());

        if (subscription.getDescription() != null) {
            body.put("description", subscription.getDescription());
        }

        try {
            JsonNode response = post("/subscriptions", body);
            String subId = response.path("id").asText();
            String status = response.path("status").asText("ACTIVE");
            String nextDue = response.path("nextDueDate").asText(subscription.getNextDueDate().toString());

            log.info("Assinatura criada no Asaas: id={}", subId);
            return new SubscriptionGatewayResponse(subId, status,
                    java.time.LocalDate.parse(nextDue));
        } catch (HttpClientErrorException e) {
            log.error("Erro ao criar assinatura no Asaas: {}", e.getResponseBodyAsString());
            throw new GatewayException("Falha ao criar assinatura no Asaas", PspProvider.ASAAS);
        }
    }

    @Override
    public void cancelSubscription(String pspSubscriptionId) {
        try {
            delete("/subscriptions/" + pspSubscriptionId);
            log.info("Assinatura cancelada no Asaas: {}", pspSubscriptionId);
        } catch (HttpClientErrorException e) {
            log.error("Erro ao cancelar assinatura Asaas {}: {}", pspSubscriptionId, e.getResponseBodyAsString());
            throw new GatewayException("Falha ao cancelar assinatura no Asaas", PspProvider.ASAAS);
        }
    }

    // ===== Saldo financeiro =====

    @Override
    public BigDecimal getBalance() {
        try {
            JsonNode response = get("/finance/balance");
            double balance = response.path("balance").asDouble(0.0);
            // Asaas retorna em reais, convertemos para centavos
            return BigDecimal.valueOf(balance * 100).setScale(0, RoundingMode.HALF_UP);
        } catch (HttpClientErrorException e) {
            log.error("Erro ao consultar saldo Asaas: {}", e.getResponseBodyAsString());
            throw new GatewayException("Falha ao consultar saldo no Asaas", PspProvider.ASAAS);
        }
    }

    // ===== Helpers privados =====

    /**
     * Cria ou localiza um cliente no Asaas pelo CPF/CNPJ.
     * Evita duplicatas e garante idempotência.
     */
    private String createOrFindCustomer(String name, String email, String cpfCnpj) {
        try {
            // Tenta localizar por CPF/CNPJ primeiro
            JsonNode search = get("/customers?cpfCnpj=" + cpfCnpj + "&limit=1");
            JsonNode data = search.path("data");
            if (data.isArray() && data.size() > 0) {
                String existingId = data.get(0).path("id").asText();
                log.debug("Cliente Asaas encontrado: cpfCnpj={}, id={}", cpfCnpj, existingId);
                return existingId;
            }
        } catch (HttpClientErrorException e) {
            log.warn("Erro ao buscar cliente no Asaas, tentando criar: {}", e.getMessage());
        }

        // Cria novo cliente
        ObjectNode body = objectMapper.createObjectNode();
        body.put("name", name);
        body.put("email", email);
        body.put("cpfCnpj", cpfCnpj);

        try {
            JsonNode response = post("/customers", body);
            String newId = response.path("id").asText();
            log.info("Cliente Asaas criado: cpfCnpj={}, id={}", cpfCnpj, newId);
            return newId;
        } catch (HttpClientErrorException e) {
            log.error("Erro ao criar cliente no Asaas: {}", e.getResponseBodyAsString());
            throw new GatewayException("Falha ao criar cliente no Asaas", PspProvider.ASAAS);
        }
    }

    private String resolvePaymentUrl(JsonNode response, Payment payment) {
        return switch (payment.getMethod()) {
            case BOLETO -> response.path("bankSlipUrl").asText(null);
            case CREDIT_CARD, DEBIT_CARD -> response.path("invoiceUrl").asText(null);
            default -> null; // PIX não tem URL, tem QR code
        };
    }

    private String mapBillingType(Payment payment) {
        return switch (payment.getMethod()) {
            case CREDIT_CARD -> "CREDIT_CARD";
            case DEBIT_CARD  -> "DEBIT_CARD";
            case PIX         -> "PIX";
            case BOLETO      -> "BOLETO";
            case CARNE       -> "BOLETO";
        };
    }

    private JsonNode post(String path, Object body) {
        HttpEntity<Object> entity = new HttpEntity<>(body, headers());
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + path, HttpMethod.POST, entity, JsonNode.class);
        return response.getBody();
    }

    private JsonNode get(String path) {
        HttpEntity<Void> entity = new HttpEntity<>(headers());
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + path, HttpMethod.GET, entity, JsonNode.class);
        return response.getBody();
    }

    private void delete(String path) {
        HttpEntity<Void> entity = new HttpEntity<>(headers());
        restTemplate.exchange(baseUrl + path, HttpMethod.DELETE, entity, Void.class);
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("access_token", apiKey);
        return headers;
    }
}
