package br.com.dompagamentos.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança — fase atual: sem autenticação.
 *
 * TODO: antes de ir para produção, implementar uma das estratégias:
 *   - Opção A (B2B API): ApiKeyAuthFilter → valida X-Api-Key header, resolve merchantId da chave
 *   - Opção B (dashboard): JwtAuthFilter  → valida JWT, extrai merchantId do claim
 *
 * O isolamento de tenant já está garantido na camada de aplicação:
 * todos os endpoints que operam sobre um recurso por ID exigem merchantId
 * e validam que o recurso pertence ao merchant informado.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .headers(headers -> headers.frameOptions(f -> f.sameOrigin())); // H2 console

        return http.build();
    }
}
