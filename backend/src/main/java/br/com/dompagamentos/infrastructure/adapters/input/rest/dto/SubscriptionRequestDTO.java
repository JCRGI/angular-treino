package br.com.dompagamentos.infrastructure.adapters.input.rest.dto;

import br.com.dompagamentos.domain.model.enums.PaymentMethod;
import br.com.dompagamentos.domain.model.enums.SubscriptionCycle;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * O merchantId é resolvido automaticamente a partir do token de autenticação —
 * não é necessário informá-lo no body.
 */
public record SubscriptionRequestDTO(

        @NotBlank(message = "Nome do cliente é obrigatório")
        @Size(max = 200)
        String customerName,

        @NotBlank(message = "E-mail do cliente é obrigatório")
        @Email
        String customerEmail,

        @NotBlank(message = "Documento do cliente é obrigatório")
        String customerDocument,

        @NotNull(message = "Valor é obrigatório")
        @Min(value = 100, message = "Valor mínimo é R$ 1,00 (100 centavos)")
        BigDecimal amountInCents,

        @NotNull(message = "Método de pagamento é obrigatório")
        PaymentMethod method,

        @NotNull(message = "Ciclo da assinatura é obrigatório")
        SubscriptionCycle cycle,

        @NotNull(message = "Data do próximo vencimento é obrigatória")
        @FutureOrPresent(message = "Data deve ser hoje ou no futuro")
        LocalDate nextDueDate,

        @Size(max = 500)
        String description
) {}
