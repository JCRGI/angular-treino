package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.ProcessPaymentInputPort;
import br.com.dompagamentos.application.ports.output.MerchantRepositoryOutputPort;
import br.com.dompagamentos.application.ports.output.PaymentGatewayOutputPort;
import br.com.dompagamentos.application.ports.output.PaymentRepositoryOutputPort;
import br.com.dompagamentos.domain.exception.MerchantNotFoundException;
import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.domain.model.enums.PaymentMethod;
import br.com.dompagamentos.domain.model.enums.PaymentStatus;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import br.com.dompagamentos.domain.service.PspRoutingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentUseCaseTest {

    @Mock private MerchantRepositoryOutputPort merchantRepository;
    @Mock private PaymentRepositoryOutputPort paymentRepository;
    @Mock private PaymentGatewayOutputPort asaasGateway;

    private ProcessPaymentUseCase useCase;

    private final Merchant activeMerchant = Merchant.create(
            "Loja Teste", "12345678000195", "loja@email.com", "11999999999");

    @BeforeEach
    void setUp() {
        PspRoutingService routing = new PspRoutingService(new BigDecimal("50000000"));
        Map<PspProvider, PaymentGatewayOutputPort> gateways = Map.of(PspProvider.ASAAS, asaasGateway);
        useCase = new ProcessPaymentUseCase(merchantRepository, paymentRepository, routing, gateways);
    }

    @Test
    @DisplayName("Deve processar pagamento e rotear para Asaas quando volume baixo")
    void shouldProcessPaymentAndRouteToAsaas() {
        when(merchantRepository.findById(activeMerchant.getId())).thenReturn(Optional.of(activeMerchant));
        when(asaasGateway.createPayment(any(), any())).thenReturn(
                new PaymentGatewayOutputPort.GatewayResponse("pay_123", "https://boleto.url", null, null));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProcessPaymentInputPort.Command command = new ProcessPaymentInputPort.Command(
                activeMerchant.getId(), "João Silva", "joao@email.com", "12345678901",
                new BigDecimal("10000"), PaymentMethod.BOLETO, "Serviço Dom Pagamentos",
                LocalDate.now().plusDays(3), 1
        );

        Payment result = useCase.execute(command);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.CONFIRMED);
        assertThat(result.getPspProvider()).isEqualTo(PspProvider.ASAAS);
        assertThat(result.getPspPaymentId()).isEqualTo("pay_123");
        verify(asaasGateway).createPayment(any(), any());
        verify(paymentRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar MerchantNotFoundException quando merchant não existe")
    void shouldThrowWhenMerchantNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(merchantRepository.findById(unknownId)).thenReturn(Optional.empty());

        ProcessPaymentInputPort.Command command = new ProcessPaymentInputPort.Command(
                unknownId, "Cliente", "c@email.com", "12345678901",
                new BigDecimal("10000"), PaymentMethod.PIX, null, null, 1
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(MerchantNotFoundException.class);
        verifyNoInteractions(asaasGateway, paymentRepository);
    }
}
