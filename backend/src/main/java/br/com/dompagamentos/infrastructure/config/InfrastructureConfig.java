package br.com.dompagamentos.infrastructure.config;

import br.com.dompagamentos.application.ports.output.PaymentGatewayOutputPort;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import br.com.dompagamentos.domain.service.PspRoutingService;
import br.com.dompagamentos.infrastructure.adapters.output.gateway.AsaasGatewayAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Configuration
public class InfrastructureConfig {

    @Value("${psp.routing.iugu-threshold-cents:50000000}")
    private BigDecimal iuguThresholdInCents;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public PspRoutingService pspRoutingService() {
        return new PspRoutingService(iuguThresholdInCents);
    }

    /**
     * Mapa de gateways indexado por PspProvider.
     * Permite o use case selecionar o gateway correto sem conhecer as implementações.
     * Apenas Asaas está ativo por enquanto — iugu será ativado com volume.
     */
    @Bean
    public Map<PspProvider, PaymentGatewayOutputPort> gateways(AsaasGatewayAdapter asaas) {
        return Map.of(PspProvider.ASAAS, asaas);
    }
}
