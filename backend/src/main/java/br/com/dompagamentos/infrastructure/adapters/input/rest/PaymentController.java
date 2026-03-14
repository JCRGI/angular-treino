package br.com.dompagamentos.infrastructure.adapters.input.rest;

import br.com.dompagamentos.application.ports.input.CancelPaymentInputPort;
import br.com.dompagamentos.application.ports.input.GetTransactionInputPort;
import br.com.dompagamentos.application.ports.input.ProcessPaymentInputPort;
import br.com.dompagamentos.application.ports.input.RefundPaymentInputPort;
import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.PaymentRequestDTO;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.PaymentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Pagamentos", description = "Criação e consulta de cobranças")
public class PaymentController {

    private final ProcessPaymentInputPort processPayment;
    private final GetTransactionInputPort getTransaction;
    private final RefundPaymentInputPort refundPayment;
    private final CancelPaymentInputPort cancelPayment;

    public PaymentController(ProcessPaymentInputPort processPayment,
                             GetTransactionInputPort getTransaction,
                             RefundPaymentInputPort refundPayment,
                             CancelPaymentInputPort cancelPayment) {
        this.processPayment = processPayment;
        this.getTransaction = getTransaction;
        this.refundPayment = refundPayment;
        this.cancelPayment = cancelPayment;
    }

    @PostMapping
    @Operation(summary = "Criar cobrança",
               description = "Cria uma nova cobrança. O PSP é selecionado automaticamente pelo volume do merchant.")
    public ResponseEntity<PaymentResponseDTO> create(@Valid @RequestBody PaymentRequestDTO request) {
        Payment payment = processPayment.execute(new ProcessPaymentInputPort.Command(
                request.merchantId(),
                request.customerName(),
                request.customerEmail(),
                request.customerDocument(),
                request.amountInCents(),
                request.method(),
                request.description(),
                request.dueDate(),
                request.installments()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponseDTO.from(payment));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cobrança por ID",
               description = "Retorna a cobrança somente se pertencer ao merchantId informado.")
    public ResponseEntity<PaymentResponseDTO> findById(
            @PathVariable UUID id,
            @Parameter(description = "ID do merchant dono da cobrança", required = true)
            @RequestParam UUID merchantId) {
        return ResponseEntity.ok(PaymentResponseDTO.from(getTransaction.findByIdAndMerchant(id, merchantId)));
    }

    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "Listar cobranças do merchant")
    public ResponseEntity<List<PaymentResponseDTO>> findByMerchant(
            @PathVariable UUID merchantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<PaymentResponseDTO> payments = getTransaction.findByMerchant(merchantId, page, size)
                .stream().map(PaymentResponseDTO::from).toList();
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Solicitar estorno",
               description = "Solicita o estorno de uma cobrança. Valida que pertence ao merchant informado.")
    public ResponseEntity<Void> refund(
            @PathVariable UUID id,
            @RequestParam UUID merchantId) {
        // Valida propriedade antes de estornar
        getTransaction.findByIdAndMerchant(id, merchantId);
        refundPayment.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancelar cobrança",
               description = "Cancela uma cobrança. Valida que pertence ao merchant informado.")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID id,
            @RequestParam UUID merchantId) {
        // Valida propriedade antes de cancelar
        getTransaction.findByIdAndMerchant(id, merchantId);
        cancelPayment.execute(id);
        return ResponseEntity.noContent().build();
    }
}
