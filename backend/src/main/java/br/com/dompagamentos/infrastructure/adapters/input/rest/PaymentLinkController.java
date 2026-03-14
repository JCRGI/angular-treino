package br.com.dompagamentos.infrastructure.adapters.input.rest;

import br.com.dompagamentos.application.ports.input.CreatePaymentLinkInputPort;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.PaymentLinkRequestDTO;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.PaymentLinkResponseDTO;
import br.com.dompagamentos.infrastructure.security.MerchantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments/link")
@Tag(name = "Payment Links", description = "Geração de links de pagamento para compartilhar via WhatsApp, e-mail ou redes sociais")
public class PaymentLinkController {

    private final CreatePaymentLinkInputPort createPaymentLink;

    public PaymentLinkController(CreatePaymentLinkInputPort createPaymentLink) {
        this.createPaymentLink = createPaymentLink;
    }

    @PostMapping
    @Operation(
            summary = "Gerar link de pagamento",
            description = """
                    Cria um link de pagamento que pode ser compartilhado via WhatsApp, e-mail ou redes sociais.
                    O pagador acessa o link e escolhe o método de pagamento (Pix, boleto ou cartão).
                    Não é necessário informar CPF do pagador no momento da criação.
                    """
    )
    public ResponseEntity<PaymentLinkResponseDTO> create(@Valid @RequestBody PaymentLinkRequestDTO request) {
        UUID merchantId = MerchantContext.currentId();
        CreatePaymentLinkInputPort.Result result = createPaymentLink.execute(
                new CreatePaymentLinkInputPort.Command(
                        merchantId,
                        request.name(),
                        request.amountInCents(),
                        request.description(),
                        request.expiresAt()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentLinkResponseDTO.from(result));
    }
}
