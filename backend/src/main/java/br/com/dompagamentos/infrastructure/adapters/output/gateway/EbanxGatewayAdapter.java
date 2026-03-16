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
 * Adapter de saída — integra com a API REST do EBANX.
 *
 * Documentação: https://developers.ebanx.com/api-reference
 *
 * Fluxo de cobrança cross-border:
 *  1. POST /ws/payment — cria a cobrança com dados do cliente e método
 *  2. Retorna checkout_url (hosted) ou pix.qr_code (PIX direto)
 *  3. Merchant recebe em BRL; EBANX converte e remete em USD/EUR via FX + SWIFT
 *
 * Autenticação: integration_key no body (não no header).
 *
 * Diferença do ASAAS:
 *  - Projetado para e-commerce internacional (cross-border settlement)
 *  - Pagador BR → recebedor em conta exterior (USD/EUR)
 *  - Suporta múltiplos países além do Brasil
 */
@Component(EbanxGatewayAdapter.BEAN_NAME)
public class EbanxGatewayAdapter implements PaymentGatewayOutputPort {

    public static final String BEAN_NAME = "EBANX_GATEWAY";
    private static final Logger log = LoggerFactory.getLogger(EbanxGatewayAdapter.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${psp.ebanx.base-url}")
    private String baseUrl;

    @Value("${psp.ebanx.integration-key}")
    private String integrationKey;

    public EbanxGatewayAdapter(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // ===== Cobrança =====

    @Override
    public GatewayResponse createPayment(Payment payment, Merchant merchant) {
        BigDecimal amountInReais = payment.getAmountInCents()
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        ObjectNode body = objectMapper.createObjectNode();
        body.put("integration_key", integrationKey);
        body.put("merchant_payment_code", payment.getId().toString());
        body.put("amount_total", amountInReais.toString());
        body.put("currency_code", "BRL");
        body.put("name", payment.getCustomerName());
        body.put("email", payment.getCustomerEmail());
        body.put("document", payment.getCustomerDocument());
        body.put("payment_type_code", mapPaymentType(payment));
        body.put("country", "br");

        if (payment.getDueDate() != null) {
            body.put("due_date", payment.getDueDate().toString());
        }
        if (payment.getDescription() != null) {
            body.put("order_number", payment.getDescription());
        }

        // Subconta EBANX do merchant (se configurada)
        if (merchant.getPspExternalId() != null) {
            body.put("merchant_sub_account_id", merchant.getPspExternalId());
        }

        try {
            JsonNode response = post("/ws/payment", body);

            if (response.path("status").asText().equals("ERROR")) {
                String errorMsg = response.path("status_message").asText("Erro desconhecido");
                log.error("Erro EBANX ao criar cobrança: {}", errorMsg);
                throw new GatewayException("Falha ao criar cobrança no EBANX: " + errorMsg, PspProvider.EBANX);
            }

            JsonNode paymentNode = response.path("payment");
            String pspId       = paymentNode.path("hash").asText();
            String checkoutUrl = paymentNode.path("checkout_url").asText(null);
            String pixQrCode   = paymentNode.path("pix").path("qr_code").asText(null);
            String pixQrBase64 = paymentNode.path("pix").path("qr_code_img_base64").asText(null);

            log.info("Cobrança criada no EBANX: hash={}, método={}", pspId, payment.getMethod());
            return new GatewayResponse(pspId, checkoutUrl, pixQrCode, pixQrBase64);

        } catch (HttpClientErrorException e) {
            log.error("Erro HTTP ao criar cobrança no EBANX: {} — {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new GatewayException("Falha ao criar cobrança no EBANX: " + e.getMessage(), PspProvider.EBANX);
        }
    }

    // ===== Subconta (sub-merchant) =====

    @Override
    public String createSubAccount(Merchant merchant) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("integration_key", integrationKey);
        body.put("name", merchant.getName());
        body.put("email", merchant.getEmail());
        body.put("document", merchant.getDocument());

        try {
            JsonNode response = post("/ws/merchant/account", body);
            if (response.path("status").asText().equals("ERROR")) {
                throw new GatewayException(
                        "Falha ao criar sub-merchant EBANX: " + response.path("status_message").asText(),
                        PspProvider.EBANX);
            }
            String subAccountId = response.path("account").path("id").asText();
            log.info("Sub-merchant EBANX criado: id={}", subAccountId);
            return subAccountId;
        } catch (HttpClientErrorException e) {
            log.error("Erro ao criar sub-merchant EBANX: {}", e.getResponseBodyAsString());
            throw new GatewayException("Falha ao criar sub-merchant no EBANX", PspProvider.EBANX);
        }
    }

    // ===== Estorno =====

    @Override
    public void refundPayment(String pspPaymentId) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("integration_key", integrationKey);
        body.put("hash", pspPaymentId);
        body.put("operation", "request");
        body.put("description", "Estorno solicitado via Dom Pagamentos");

        try {
            JsonNode response = post("/ws/refund", body);
            if (response.path("status").asText().equals("ERROR")) {
                throw new GatewayException(
                        "Falha ao estornar no EBANX: " + response.path("status_message").asText(),
                        PspProvider.EBANX);
            }
            log.info("Estorno solicitado no EBANX para hash: {}", pspPaymentId);
        } catch (HttpClientErrorException e) {
            log.error("Erro ao estornar no EBANX {}: {}", pspPaymentId, e.getResponseBodyAsString());
            throw new GatewayException("Falha ao estornar pagamento no EBANX", PspProvider.EBANX);
        }
    }

    // ===== Assinaturas — não suportadas pelo EBANX nesta plataforma =====

    @Override
    public SubscriptionGatewayResponse createSubscription(Subscription subscription, Merchant merchant) {
        throw new GatewayException(
                "Assinaturas recorrentes não são suportadas via EBANX nesta plataforma. Use ASAAS.",
                PspProvider.EBANX);
    }

    @Override
    public void cancelSubscription(String pspSubscriptionId) {
        throw new GatewayException(
                "Assinaturas recorrentes não são suportadas via EBANX nesta plataforma. Use ASAAS.",
                PspProvider.EBANX);
    }

    // ===== Saldo =====

    @Override
    public BigDecimal getBalance() {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("integration_key", integrationKey);

        try {
            JsonNode response = post("/ws/balance", body);
            if (response.path("status").asText().equals("ERROR")) {
                throw new GatewayException(
                        "Falha ao consultar saldo EBANX: " + response.path("status_message").asText(),
                        PspProvider.EBANX);
            }
            // EBANX retorna o saldo em reais
            double balance = response.path("balance_info").path("available").asDouble(0.0);
            return BigDecimal.valueOf(balance * 100).setScale(0, RoundingMode.HALF_UP);
        } catch (HttpClientErrorException e) {
            log.error("Erro ao consultar saldo EBANX: {}", e.getResponseBodyAsString());
            throw new GatewayException("Falha ao consultar saldo no EBANX", PspProvider.EBANX);
        }
    }

    // ===== Helpers privados =====

    private String mapPaymentType(Payment payment) {
        return switch (payment.getMethod()) {
            case CREDIT_CARD  -> "creditcard";
            case DEBIT_CARD   -> "debitcard";
            case PIX          -> "pix";
            case BOLETO, CARNE -> "boleto";
        };
    }

    private JsonNode post(String path, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + path, HttpMethod.POST, entity, JsonNode.class);
        return response.getBody();
    }
}
