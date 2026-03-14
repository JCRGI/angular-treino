package br.com.dompagamentos.infrastructure.adapters.input.rest.dto;

import br.com.dompagamentos.domain.model.enums.PaymentMethod;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentRequestDTO(

        @NotNull(message = "merchantId é obrigatório")
        UUID merchantId,

        @NotBlank(message = "Nome do cliente é obrigatório")
        @Size(max = 200)
        String customerName,

        @NotBlank(message = "E-mail do cliente é obrigatório")
        @Email(message = "E-mail inválido")
        String customerEmail,

        @NotBlank(message = "CPF/CNPJ do cliente é obrigatório")
        String customerDocument,

        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "100", message = "Valor mínimo é R$ 1,00 (100 centavos)")
        BigDecimal amountInCents,

        @NotNull(message = "Método de pagamento é obrigatório")
        PaymentMethod method,

        @Size(max = 500)
        String description,

        LocalDate dueDate,

        @Min(value = 1, message = "Parcelas mínimas: 1")
        @Max(value = 21, message = "Parcelas máximas: 21")
        Integer installments
) {}
