package br.com.dompagamentos.infrastructure.adapters.input.rest.dto;

import java.math.BigDecimal;

public record BalanceResponseDTO(
        BigDecimal balanceInCents,
        String currency
) {
    public static BalanceResponseDTO of(BigDecimal balanceInCents) {
        return new BalanceResponseDTO(balanceInCents, "BRL");
    }
}
