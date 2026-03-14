package br.com.dompagamentos.domain.service;

import br.com.dompagamentos.domain.model.Merchant;
import br.com.dompagamentos.domain.model.enums.PspProvider;

import java.math.BigDecimal;

/**
 * Domain Service responsável pela lógica de roteamento entre PSPs.
 *
 * Regra de negócio central da Dom Pagamentos:
 *  - Merchants com volume mensal < threshold → ASAAS (sem mensalidade)
 *  - Merchants com volume mensal >= threshold → IUGU (MDR negociado)
 *
 * Mantida no domínio pois é uma regra de negócio pura,
 * independente de qualquer infraestrutura.
 */
public class PspRoutingService {

    private final BigDecimal iuguThresholdInCents;

    public PspRoutingService(BigDecimal iuguThresholdInCents) {
        this.iuguThresholdInCents = iuguThresholdInCents;
    }

    public PspProvider route(Merchant merchant) {
        if (merchant.getPreferredPsp() != null) {
            return merchant.getPreferredPsp();
        }
        return isEligibleForIugu(merchant) ? PspProvider.IUGU : PspProvider.ASAAS;
    }

    public boolean isEligibleForIugu(Merchant merchant) {
        return merchant.getMonthlyVolumeInCents()
                .compareTo(iuguThresholdInCents) >= 0;
    }
}
