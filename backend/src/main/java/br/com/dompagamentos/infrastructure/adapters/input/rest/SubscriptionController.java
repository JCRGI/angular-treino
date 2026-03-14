package br.com.dompagamentos.infrastructure.adapters.input.rest;

import br.com.dompagamentos.application.ports.input.CancelSubscriptionInputPort;
import br.com.dompagamentos.application.ports.input.CreateSubscriptionInputPort;
import br.com.dompagamentos.application.ports.input.GetSubscriptionInputPort;
import br.com.dompagamentos.domain.model.Subscription;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.SubscriptionRequestDTO;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.SubscriptionResponseDTO;
import br.com.dompagamentos.infrastructure.security.MerchantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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
    @Operation(summary = "Criar assinatura")
    public ResponseEntity<SubscriptionResponseDTO> create(@Valid @RequestBody SubscriptionRequestDTO dto) {
        UUID merchantId = MerchantContext.currentId();
        Subscription subscription = createSubscription.execute(new CreateSubscriptionInputPort.Command(
                merchantId,
                dto.customerName(), dto.customerEmail(), dto.customerDocument(),
                dto.amountInCents(), dto.method(), dto.cycle(), dto.nextDueDate(), dto.description()
        ));
        return ResponseEntity
                .created(URI.create("/api/v1/subscriptions/" + subscription.getId()))
                .body(SubscriptionResponseDTO.from(subscription));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar assinatura por ID")
    public ResponseEntity<SubscriptionResponseDTO> findById(@PathVariable UUID id) {
        UUID merchantId = MerchantContext.currentId();
        Subscription sub = getSubscription.findById(id);
        sub.assertOwnedBy(merchantId);
        return ResponseEntity.ok(SubscriptionResponseDTO.from(sub));
    }

    @GetMapping
    @Operation(summary = "Listar assinaturas do merchant autenticado")
    public ResponseEntity<List<SubscriptionResponseDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID merchantId = MerchantContext.currentId();
        return ResponseEntity.ok(
                getSubscription.findByMerchant(merchantId, page, size)
                        .stream().map(SubscriptionResponseDTO::from).toList()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar assinatura")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        UUID merchantId = MerchantContext.currentId();
        getSubscription.findById(id).assertOwnedBy(merchantId);
        cancelSubscription.execute(id);
        return ResponseEntity.noContent().build();
    }
}
