package br.com.dompagamentos.application.ports.output;

import br.com.dompagamentos.domain.model.Merchant;

import java.util.Optional;
import java.util.UUID;

/**
 * Secondary Port — persistência de merchants.
 * O domínio nunca conhece JPA/SQL; conhece apenas esta interface.
 */
public interface MerchantRepositoryOutputPort {

    Merchant save(Merchant merchant);

    Optional<Merchant> findById(UUID id);

    Optional<Merchant> findByDocument(String document);

    boolean existsByDocument(String document);
}
