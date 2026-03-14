package br.com.dompagamentos.infrastructure.adapters.input.rest;

import br.com.dompagamentos.application.ports.input.CreateMerchantInputPort;
import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.MerchantRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchants")
@Tag(name = "Merchants", description = "Cadastro e gerenciamento de lojistas")
@SecurityRequirement(name = "bearerAuth")
public class MerchantController {

    private final CreateMerchantInputPort createMerchant;

    public MerchantController(CreateMerchantInputPort createMerchant) {
        this.createMerchant = createMerchant;
    }

    @PostMapping
    @Operation(summary = "Cadastrar merchant", description = "Registra um novo lojista e cria subcontas nos PSPs automaticamente.")
    public ResponseEntity<MerchantResponseDTO> create(@Valid @RequestBody MerchantRequestDTO request) {
        Merchant merchant = createMerchant.execute(
                new CreateMerchantInputPort.Command(
                        request.name(), request.document(), request.email(), request.phone(),
                        request.callbackUrl()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(MerchantResponseDTO.from(merchant));
    }

    record MerchantResponseDTO(
            java.util.UUID id,
            String name,
            String document,
            String email,
            String preferredPsp,
            String callbackUrl,
            java.time.LocalDateTime createdAt
    ) {
        static MerchantResponseDTO from(Merchant m) {
            return new MerchantResponseDTO(
                    m.getId(), m.getName(), m.getDocument(), m.getEmail(),
                    m.getPreferredPsp().name(), m.getCallbackUrl(), m.getCreatedAt()
            );
        }
    }
}
