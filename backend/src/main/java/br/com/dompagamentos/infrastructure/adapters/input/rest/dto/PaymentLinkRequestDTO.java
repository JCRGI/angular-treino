package br.com.dompagamentos.infrastructure.adapters.input.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de entrada para criação de payment link.
 *
 * Não exige CPF nem método de pagamento — o pagador fornece esses dados
 * ao acessar o link gerado.
 */
public record PaymentLinkRequestDTO(

        @NotBlank(message = "Nome do link é obrigatório")
        @Size(max = 200)
        String name,

        @DecimalMin(value = "100", message = "Valor mínimo é R$ 1,00 (100 centavos)")
        BigDecimal amountInCents,

        @Size(max = 500)
        String description,

        LocalDate expiresAt
) {}
