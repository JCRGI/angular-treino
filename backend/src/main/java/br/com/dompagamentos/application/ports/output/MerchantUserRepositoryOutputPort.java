package br.com.dompagamentos.application.ports.output;

import br.com.dompagamentos.domain.model.MerchantUser;

import java.util.Optional;
import java.util.UUID;

public interface MerchantUserRepositoryOutputPort {

    MerchantUser save(MerchantUser user);

    Optional<MerchantUser> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMerchantId(UUID merchantId);
}
