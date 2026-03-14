package br.com.dompagamentos.infrastructure.adapters.input.rest;

import br.com.dompagamentos.application.ports.input.GetTransactionInputPort;
import br.com.dompagamentos.application.ports.input.ProcessPaymentInputPort;
import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.PaymentRequestDTO;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.PaymentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final ProcessPaymentInputPort processPayment;
    private final GetTransactionInputPort getTransaction;

    public PaymentController(ProcessPaymentInputPort processPayment,
                             GetTransactionInputPort getTransaction) {
        this.processPayment = processPayment;
        this.getTransaction = getTransaction;
    }

    @PostMapping
    @Operation(summary = "Criar cobrança", description = "Cria uma nova cobrança para um cliente. O PSP é selecionado automaticamente com base no volume do merchant.")
    public ResponseEntity<PaymentResponseDTO> create(@Valid @RequestBody PaymentRequestDTO request) {
        var command = new ProcessPaymentInputPort.Command(
                request.merchantId(),
                request.customerName(),
                request.customerEmail(),
                request.customerDocument(),
                request.amountInCents(),
                request.method(),
                request.description(),
                request.dueDate(),
                request.installments()
        );
        Payment payment = processPayment.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponseDTO.from(payment));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cobrança por ID")
    public ResponseEntity<PaymentResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(PaymentResponseDTO.from(getTransaction.findById(id)));
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
}
