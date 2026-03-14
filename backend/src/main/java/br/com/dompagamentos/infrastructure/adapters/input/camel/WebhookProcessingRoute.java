package br.com.dompagamentos.infrastructure.adapters.input.camel;

import br.com.dompagamentos.application.ports.output.MerchantRepositoryOutputPort;
import br.com.dompagamentos.application.ports.output.PaymentRepositoryOutputPort;
import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.domain.model.enums.PaymentStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Rotas Camel para processamento de webhooks dos PSPs.
 *
 * Por que Camel aqui?
 * - Retry automático com backoff exponencial em caso de falha
 * - Dead Letter Channel para webhooks que não conseguimos processar
 * - Transformação de payload (Asaas → domínio, iugu → domínio)
 * - Circuit breaker via Resilience4j
 * - Observabilidade via Micrometer
 */
@Component
public class WebhookProcessingRoute extends RouteBuilder {

    // Mapeamento de eventos Asaas → status de domínio
    // Referência: https://docs.asaas.com/reference/webhook
    private static final Map<String, PaymentStatus> ASAAS_STATUS_MAP = Map.ofEntries(
            Map.entry("PAYMENT_CONFIRMED", PaymentStatus.CONFIRMED),
            Map.entry("PAYMENT_RECEIVED", PaymentStatus.RECEIVED),
            Map.entry("PAYMENT_OVERDUE", PaymentStatus.OVERDUE),
            Map.entry("PAYMENT_DELETED", PaymentStatus.CANCELLED),
            Map.entry("PAYMENT_RESTORED", PaymentStatus.PENDING),
            Map.entry("PAYMENT_REFUNDED", PaymentStatus.REFUNDED),
            Map.entry("PAYMENT_PARTIALLY_REFUNDED", PaymentStatus.REFUND_REQUESTED),
            Map.entry("PAYMENT_CHARGEBACK_REQUESTED", PaymentStatus.CHARGEBACK_REQUESTED),
            Map.entry("PAYMENT_CHARGEBACK_DISPUTE", PaymentStatus.CHARGEBACK_DISPUTE),
            Map.entry("PAYMENT_AWAITING_CHARGEBACK_REVERSAL", PaymentStatus.AWAITING_CHARGEBACK_REVERSAL),
            Map.entry("PAYMENT_DUNNING_REQUESTED", PaymentStatus.DUNNING_REQUESTED),
            Map.entry("PAYMENT_DUNNING_RECEIVED", PaymentStatus.DUNNING_RECEIVED),
            Map.entry("PAYMENT_AWAITING_RISK_ANALYSIS", PaymentStatus.PROCESSING)
    );

    // Mapeamento de status iugu → domínio
    private static final Map<String, PaymentStatus> IUGU_STATUS_MAP = Map.of(
            "invoice.status_changed.paid", PaymentStatus.RECEIVED,
            "invoice.status_changed.canceled", PaymentStatus.CANCELLED,
            "invoice.status_changed.refunded", PaymentStatus.REFUNDED,
            "invoice.status_changed.expired", PaymentStatus.OVERDUE
    );

    private final PaymentRepositoryOutputPort paymentRepository;
    private final MerchantRepositoryOutputPort merchantRepository;
    private final ObjectMapper objectMapper;

    public WebhookProcessingRoute(PaymentRepositoryOutputPort paymentRepository,
                                  MerchantRepositoryOutputPort merchantRepository,
                                  ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.merchantRepository = merchantRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void configure() {

        // ===== Dead Letter Channel — fallback para webhooks com erro =====
        errorHandler(deadLetterChannel("log:br.com.dompagamentos.webhook.dlq?level=ERROR")
                .maximumRedeliveries(3)
                .redeliveryDelay(2000)
                .backOffMultiplier(2)
                .useExponentialBackOff()
                .logRetryAttempted(true));

        // ===== Rota Asaas Webhook =====
        from("direct:asaas-webhook")
                .routeId("asaas-webhook-processor")
                .log("Processando webhook Asaas: ${body}")
                .process(exchange -> {
                    JsonNode payload = exchange.getIn().getBody(JsonNode.class);
                    String event = payload.path("event").asText();
                    String pspPaymentId = payload.path("payment").path("id").asText();

                    PaymentStatus newStatus = ASAAS_STATUS_MAP.get(event);
                    if (newStatus == null) {
                        log.debug("Evento Asaas ignorado: {}", event);
                        exchange.setProperty("skip", true);
                        return;
                    }

                    exchange.getIn().setHeader("pspPaymentId", pspPaymentId);
                    exchange.getIn().setHeader("newStatus", newStatus);
                })
                .filter(exchangeProperty("skip").isNull())
                .to("direct:update-payment-status");

        // ===== Rota iugu Webhook =====
        from("direct:iugu-webhook")
                .routeId("iugu-webhook-processor")
                .log("Processando webhook iugu: ${body}")
                .process(exchange -> {
                    JsonNode payload = exchange.getIn().getBody(JsonNode.class);
                    String event = payload.path("event").asText();
                    String pspPaymentId = payload.path("data").path("id").asText();

                    PaymentStatus newStatus = IUGU_STATUS_MAP.get(event);
                    if (newStatus == null) {
                        log.debug("Evento iugu ignorado: {}", event);
                        exchange.setProperty("skip", true);
                        return;
                    }

                    exchange.getIn().setHeader("pspPaymentId", pspPaymentId);
                    exchange.getIn().setHeader("newStatus", newStatus);
                })
                .filter(exchangeProperty("skip").isNull())
                .to("direct:update-payment-status");

        // ===== Rota comum: atualizar status no banco =====
        from("direct:update-payment-status")
                .routeId("update-payment-status")
                .process(exchange -> {
                    String pspPaymentId = exchange.getIn().getHeader("pspPaymentId", String.class);
                    PaymentStatus newStatus = exchange.getIn().getHeader("newStatus", PaymentStatus.class);

                    Optional<Payment> paymentOpt = paymentRepository.findByPspPaymentId(pspPaymentId);
                    if (paymentOpt.isEmpty()) {
                        log.warn("Pagamento não encontrado para pspPaymentId: {}", pspPaymentId);
                        return;
                    }

                    Payment payment = paymentOpt.get();
                    payment.updateStatusFromWebhook(newStatus);
                    paymentRepository.save(payment);

                    log.info("Pagamento {} atualizado para status {} via webhook", payment.getId(), newStatus);

                    // Passa o pagamento salvo para a próxima etapa
                    exchange.getIn().setBody(payment);
                })
                .process(exchange -> {
                    Payment payment = exchange.getIn().getBody(Payment.class);
                    if (payment == null) return;

                    Optional<Merchant> merchantOpt = merchantRepository.findById(payment.getMerchantId());
                    if (merchantOpt.isEmpty()) return;

                    Merchant merchant = merchantOpt.get();
                    String callbackUrl = merchant.getCallbackUrl();
                    if (callbackUrl == null || callbackUrl.isBlank()) return;

                    OutboundWebhookPayload outboundPayload = new OutboundWebhookPayload(
                            "PAYMENT_STATUS_CHANGED",
                            payment.getId().toString(),
                            payment.getMerchantId().toString(),
                            payment.getStatus().name(),
                            payment.getAmountInCents(),
                            payment.getPspPaymentId(),
                            payment.getUpdatedAt().toString()
                    );

                    exchange.getIn().setHeader("callbackUrl", callbackUrl);
                    exchange.getIn().setBody(outboundPayload);
                })
                .filter(header("callbackUrl").isNotNull())
                .to("direct:outbound-webhook");

        // ===== Rota de saída: notifica o merchant via HTTP POST =====
        from("direct:outbound-webhook")
                .routeId("outbound-webhook-dispatcher")
                .process(exchange -> {
                    OutboundWebhookPayload payload = exchange.getIn().getBody(OutboundWebhookPayload.class);
                    exchange.getIn().setBody(objectMapper.writeValueAsString(payload));
                    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
                })
                .toD("${header.callbackUrl}?throwExceptionOnFailure=true")
                .log("Webhook de saída enviado para ${header.callbackUrl} — paymentId ${header.callbackUrl}");
    }
}
