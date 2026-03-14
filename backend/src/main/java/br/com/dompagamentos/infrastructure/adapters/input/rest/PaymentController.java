package br.com.dompagamentos.infrastructure.adapters.input.rest;

import br.com.dompagamentos.application.ports.input.CancelPaymentInputPort;
import br.com.dompagamentos.application.ports.input.GetTransactionInputPort;
import br.com.dompagamentos.application.ports.input.ProcessPaymentInputPort;
import br.com.dompagamentos.application.ports.input.RefundPaymentInputPort;
import br.com.dompagamentos.domain.model.Payment;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.PaymentRequestDTO;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.PaymentResponseDTO;
import br.com.dompagamentos.infrastructure.security.MerchantContext;
import io.swagger.v3.oas.annotations.Operation;
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
               description = "Cria uma cobrança para o merchant autenticado. O PSP é selecionado automaticamente.")
    public ResponseEntity<PaymentResponseDTO> create(@Valid @RequestBody PaymentRequestDTO request) {
        UUID merchantId = MerchantContext.currentId();
        Payment payment = processPayment.execute(new ProcessPaymentInputPort.Command(
                merchantId,
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
    @Operation(summary = "Buscar cobrança por ID")
    public ResponseEntity<PaymentResponseDTO> findById(@PathVariable UUID id) {
        UUID merchantId = MerchantContext.currentId();
        return ResponseEntity.ok(PaymentResponseDTO.from(getTransaction.findByIdAndMerchant(id, merchantId)));
    }

    @GetMapping
    @Operation(summary = "Listar cobranças do merchant autenticado")
    public ResponseEntity<List<PaymentResponseDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID merchantId = MerchantContext.currentId();
        return ResponseEntity.ok(
                getTransaction.findByMerchant(merchantId, page, size)
                        .stream().map(PaymentResponseDTO::from).toList()
        );
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Solicitar estorno")
    public ResponseEntity<Void> refund(@PathVariable UUID id) {
        UUID merchantId = MerchantContext.currentId();
        getTransaction.findByIdAndMerchant(id, merchantId); // valida propriedade
        refundPayment.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancelar cobrança")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        UUID merchantId = MerchantContext.currentId();
        getTransaction.findByIdAndMerchant(id, merchantId); // valida propriedade
        cancelPayment.execute(id);
        return ResponseEntity.noContent().build();
    }
}
