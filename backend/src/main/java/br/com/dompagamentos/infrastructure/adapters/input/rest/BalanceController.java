package br.com.dompagamentos.infrastructure.adapters.input.rest;

import br.com.dompagamentos.application.ports.input.GetBalanceInputPort;
import br.com.dompagamentos.infrastructure.adapters.input.rest.dto.BalanceResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expõe o saldo disponível na conta Asaas.
 * Documentação: https://docs.asaas.com/reference/recuperar-saldo-da-conta
 */
@RestController
@RequestMapping("/api/v1/financial")
@Tag(name = "Financeiro", description = "Informações financeiras da conta")
@SecurityRequirement(name = "bearerAuth")
public class BalanceController {

    private final GetBalanceInputPort getBalance;

    public BalanceController(GetBalanceInputPort getBalance) {
        this.getBalance = getBalance;
    }

    @GetMapping("/balance")
    @Operation(summary = "Saldo disponível",
               description = "Retorna o saldo disponível na conta Asaas, em centavos.")
    public ResponseEntity<BalanceResponseDTO> balance() {
        return ResponseEntity.ok(BalanceResponseDTO.of(getBalance.execute()));
    }
}
