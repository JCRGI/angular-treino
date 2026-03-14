package br.com.dompagamentos.infrastructure.adapters.input.camel;

import br.com.dompagamentos.application.ports.output.PaymentRepositoryOutputPort;
import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.domain.model.enums.PaymentStatus;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
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

    // Mapeamento de status Asaas → domínio
    private static final Map<String, PaymentStatus> ASAAS_STATUS_MAP = Map.of(
            "PAYMENT_CONFIRMED", PaymentStatus.CONFIRMED,
            "PAYMENT_RECEIVED", PaymentStatus.RECEIVED,
            "PAYMENT_OVERDUE", PaymentStatus.OVERDUE,
            "PAYMENT_DELETED", PaymentStatus.CANCELLED,
            "PAYMENT_REFUNDED", PaymentStatus.REFUNDED,
            "PAYMENT_CHARGEBACK_REQUESTED", PaymentStatus.CHARGEBACK_REQUESTED,
            "PAYMENT_CHARGEBACK_DISPUTE", PaymentStatus.CHARGEBACK_DISPUTE
    );

    // Mapeamento de status iugu → domínio
    private static final Map<String, PaymentStatus> IUGU_STATUS_MAP = Map.of(
            "invoice.status_changed.paid", PaymentStatus.RECEIVED,
            "invoice.status_changed.canceled", PaymentStatus.CANCELLED,
            "invoice.status_changed.refunded", PaymentStatus.REFUNDED,
            "invoice.status_changed.expired", PaymentStatus.OVERDUE
    );

    private final PaymentRepositoryOutputPort paymentRepository;

    public WebhookProcessingRoute(PaymentRepositoryOutputPort paymentRepository) {
        this.paymentRepository = paymentRepository;
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
                });
    }
}
