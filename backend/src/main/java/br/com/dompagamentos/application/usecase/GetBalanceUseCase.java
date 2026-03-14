package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.GetBalanceInputPort;
import br.com.dompagamentos.application.ports.output.PaymentGatewayOutputPort;
import br.com.dompagamentos.domain.model.enums.PspProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class GetBalanceUseCase implements GetBalanceInputPort {

    private final Map<PspProvider, PaymentGatewayOutputPort> gateways;

    public GetBalanceUseCase(Map<PspProvider, PaymentGatewayOutputPort> gateways) {
        this.gateways = gateways;
    }

    @Override
    public BigDecimal execute() {
        return gateways.get(PspProvider.ASAAS).getBalance();
    }
}
