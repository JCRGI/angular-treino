package br.com.dompagamentos.infrastructure.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Helper para acessar o merchantId do merchant autenticado na thread atual.
 *
 * Ambos os filtros (ApiKeyAuthFilter e JwtAuthFilter) populam o principal
 * do SecurityContext com o UUID do merchant. Este helper simplesmente lê esse valor.
 *
 * Uso nos controllers:
 *   UUID merchantId = MerchantContext.currentId();
 */
public final class MerchantContext {

    private MerchantContext() {}

    public static UUID currentId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UUID id) {
            return id;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação necessária.");
    }
}
