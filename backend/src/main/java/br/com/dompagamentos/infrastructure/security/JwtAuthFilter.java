package br.com.dompagamentos.infrastructure.security;

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
import java.util.UUID;

/**
 * Filtro de autenticação via JWT (modo dashboard).
 *
 * Header: Authorization: Bearer <token>
 *
 * Fluxo:
 *  1. Lê o header Authorization
 *  2. Extrai e valida o token JWT
 *  3. Extrai merchantId do claim "merchantId"
 *  4. Define o merchantId como principal no SecurityContext
 *
 * Obs: se X-Api-Key já autenticou o request, este filtro não sobrescreve.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(7);
            try {
                if (jwtService.isValid(token)) {
                    UUID merchantId = jwtService.extractMerchantId(token);

                    var auth = new UsernamePasswordAuthenticationToken(
                            merchantId,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_MERCHANT"))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("Autenticado via JWT: merchantId={}", merchantId);
                } else {
                    log.warn("JWT inválido ou expirado recebido.");
                }
            } catch (Exception e) {
                log.error("Erro ao processar JWT", e);
            }
        }

        chain.doFilter(request, response);
    }
}
