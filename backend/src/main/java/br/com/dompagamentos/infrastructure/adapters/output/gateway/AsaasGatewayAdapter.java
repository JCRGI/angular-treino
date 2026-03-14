package br.com.dompagamentos.infrastructure.adapters.output.gateway;

import br.com.dompagamentos.application.ports.output.PaymentGatewayOutputPort;
import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.domain.model.Payment;
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
 * Adapter de saída — integra com a API REST do Asaas.
 * Implementa PaymentGatewayOutputPort para o PSP Asaas.
 *
 * Documentação: https://docs.asaas.com
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

    @Override
    public GatewayResponse createPayment(Payment payment, Merchant merchant) {
        ObjectNode body = objectMapper.createObjectNode();

        // Valor em reais (Asaas usa reais, não centavos)
        BigDecimal valueInReais = payment.getAmountInCents()
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        body.put("billingType", mapMethod(payment));
        body.put("value", valueInReais.doubleValue());
        body.put("dueDate", payment.getDueDate() != null
                ? payment.getDueDate().toString()
                : java.time.LocalDate.now().plusDays(3).toString());

        // Cliente
        ObjectNode customer = objectMapper.createObjectNode();
        customer.put("name", payment.getCustomerName());
        customer.put("email", payment.getCustomerEmail());
        customer.put("cpfCnpj", payment.getCustomerDocument());
        body.set("customer", customer);

        if (payment.getDescription() != null) body.put("description", payment.getDescription());
        if (payment.getInstallments() != null && payment.getInstallments() > 1) {
            body.put("installmentCount", payment.getInstallments());
            body.put("installmentValue", valueInReais.divide(
                    BigDecimal.valueOf(payment.getInstallments()), 2, RoundingMode.HALF_UP).doubleValue());
        }

        // Usar subaccount do merchant se disponível
        if (merchant.getPspExternalId() != null) {
            body.put("walletId", merchant.getPspExternalId());
        }

        try {
            JsonNode response = post("/charges", body);
            return new GatewayResponse(
                    response.path("id").asText(),
                    response.path("bankSlipUrl").asText(null),
                    response.path("pix").path("payload").asText(null),
                    response.path("pix").path("encodedImage").asText(null)
            );
        } catch (HttpClientErrorException e) {
            log.error("Erro ao criar cobrança no Asaas: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new GatewayException("Falha ao criar cobrança no Asaas: " + e.getMessage(), PspProvider.ASAAS);
        }
    }

    @Override
    public String createSubAccount(Merchant merchant) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("name", merchant.getName());
        body.put("email", merchant.getEmail());
        body.put("cpfCnpj", merchant.getDocument());
        if (merchant.getPhone() != null) body.put("mobilePhone", merchant.getPhone());
        body.put("incomeValue", 1000.0);

        try {
            JsonNode response = post("/accounts", body);
            return response.path("walletId").asText();
        } catch (HttpClientErrorException e) {
            log.error("Erro ao criar subconta Asaas: {}", e.getResponseBodyAsString());
            throw new GatewayException("Falha ao criar subconta no Asaas", PspProvider.ASAAS);
        }
    }

    @Override
    public void refundPayment(String pspPaymentId) {
        try {
            post("/charges/" + pspPaymentId + "/refund", objectMapper.createObjectNode());
        } catch (HttpClientErrorException e) {
            log.error("Erro ao estornar pagamento Asaas {}: {}", pspPaymentId, e.getResponseBodyAsString());
            throw new GatewayException("Falha ao estornar pagamento no Asaas", PspProvider.ASAAS);
        }
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
        headers.set("access_token", apiKey);
        return headers;
    }

    private String mapMethod(Payment payment) {
        return switch (payment.getMethod()) {
            case CREDIT_CARD -> "CREDIT_CARD";
            case DEBIT_CARD -> "DEBIT_CARD";
            case PIX -> "PIX";
            case BOLETO -> "BOLETO";
            default -> "BOLETO";
        };
    }
}
