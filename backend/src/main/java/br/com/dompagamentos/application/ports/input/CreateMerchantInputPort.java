package br.com.dompagamentos.application.ports.input;

import br.com.dompagamentos.domain.model.Merchant;

/**
 * Primary Port — contrato para cadastro de novo merchant.
 */
public interface CreateMerchantInputPort {

    Merchant execute(Command command);

    record Command(
            String name,
            String document,
            String email,
            String phone
    ) {}
}
