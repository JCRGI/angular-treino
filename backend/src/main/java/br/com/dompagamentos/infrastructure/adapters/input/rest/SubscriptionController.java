package br.com.dompagamentos.infrastructure.adapters.input.rest;

import br.com.dompagamentos.application.ports.input.CancelSubscriptionInputPort;
import br.com.dompagamentos.application.ports.input.CreateSubscriptionInputPort;
import br.com.dompagamentos.application.ports.input.GetSubscriptionInputPort;
import br.com.dompagamentos.domain.model.Subscription;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.SubscriptionRequestDTO;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.SubscriptionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST controller para assinaturas recorrentes.
 * Usa a API de subscriptions do Asaas: https://docs.asaas.com/reference/criar-nova-assinatura
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscriptions", description = "Gerenciamento de assinaturas recorrentes")
public class SubscriptionController {

    private final CreateSubscriptionInputPort createSubscription;
    private final GetSubscriptionInputPort getSubscription;
    private final CancelSubscriptionInputPort cancelSubscription;

    public SubscriptionController(CreateSubscriptionInputPort createSubscription,
                                  GetSubscriptionInputPort getSubscription,
                                  CancelSubscriptionInputPort cancelSubscription) {
        this.createSubscription = createSubscription;
        this.getSubscription = getSubscription;
        this.cancelSubscription = cancelSubscription;
    }

    @PostMapping
    @Operation(summary = "Criar assinatura", description = "Cria uma assinatura recorrente no Asaas.")
    public ResponseEntity<SubscriptionResponseDTO> create(@Valid @RequestBody SubscriptionRequestDTO dto) {
        Subscription subscription = createSubscription.execute(new CreateSubscriptionInputPort.Command(
                dto.merchantId(),
                dto.customerName(),
                dto.customerEmail(),
                dto.customerDocument(),
                dto.amountInCents(),
                dto.method(),
                dto.cycle(),
                dto.nextDueDate(),
                dto.description()
        ));
        return ResponseEntity
                .created(URI.create("/api/v1/subscriptions/" + subscription.getId()))
                .body(SubscriptionResponseDTO.from(subscription));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar assinatura por ID")
    public ResponseEntity<SubscriptionResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(SubscriptionResponseDTO.from(getSubscription.findById(id)));
    }

    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "Listar assinaturas do merchant")
    public ResponseEntity<List<SubscriptionResponseDTO>> findByMerchant(
            @PathVariable UUID merchantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<SubscriptionResponseDTO> result = getSubscription.findByMerchant(merchantId, page, size)
                .stream()
                .map(SubscriptionResponseDTO::from)
                .toList();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar assinatura")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        cancelSubscription.execute(id);
        return ResponseEntity.noContent().build();
    }
}
