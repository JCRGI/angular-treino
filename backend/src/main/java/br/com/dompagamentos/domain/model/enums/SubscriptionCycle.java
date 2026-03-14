package br.com.dompagamentos.domain.model.enums;

/**
 * Ciclos de cobrança suportados pelo Asaas.
 * Mapeados diretamente para os valores da API: https://docs.asaas.com/reference/criar-nova-assinatura
 */
public enum SubscriptionCycle {
    WEEKLY,
    BIWEEKLY,
    MONTHLY,
    QUARTERLY,
    SEMIANNUALLY,
    YEARLY
}
