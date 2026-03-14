package br.com.dompagamentos.infrastructure.security;

import br.com.dompagamentos.application.ports.output.MerchantApiKeyRepositoryOutputPort;
import br.com.dompagamentos.domain.model.MerchantApiKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Filtro de autenticação via API Key (modo B2B).
 *
 * Header: X-Api-Key: dpag_<chave>
 *
 * Fluxo:
 *  1. Lê o header X-Api-Key
 *  2. Calcula SHA-256 da chave recebida
 *  3. Busca no banco pelo hash
 *  4. Se encontrado e ativo → autentica com o merchantId da chave
 *  5. Registra o last_used_at para auditoria
 */
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthFilter.class);
    static final String HEADER = "X-Api-Key";

    private final MerchantApiKeyRepositoryOutputPort apiKeyRepository;
    private final ApiKeyService apiKeyService;

    public ApiKeyAuthFilter(MerchantApiKeyRepositoryOutputPort apiKeyRepository,
                            ApiKeyService apiKeyService) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String rawKey = request.getHeader(HEADER);

        if (rawKey != null && !rawKey.isBlank()
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String hash = apiKeyService.hash(rawKey);
                Optional<MerchantApiKey> found = apiKeyRepository.findByKeyHash(hash);

                if (found.isPresent() && found.get().isActive()) {
                    MerchantApiKey apiKey = found.get();

                    var auth = new UsernamePasswordAuthenticationToken(
                            apiKey.getMerchantId(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_MERCHANT"))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // Registra uso (fire-and-forget — não bloqueia o request)
                    apiKey.recordUsage();
                    apiKeyRepository.save(apiKey);

                    log.debug("Autenticado via API Key: merchantId={}", apiKey.getMerchantId());
                } else {
                    log.warn("API Key inválida ou revogada recebida.");
                }
            } catch (Exception e) {
                log.error("Erro ao validar API Key", e);
            }
        }

        chain.doFilter(request, response);
    }
}
