package br.com.dompagamentos.infrastructure.security;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Geração e hash de API Keys.
 *
 * Formato da chave:  dpag_<40 hex chars aleatórios>
 * Exemplo:           dpag_a3f9b2c1d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9
 *
 * Só o hash SHA-256 é armazenado no banco.
 * A chave em texto plano é retornada UMA única vez no momento da criação.
 */
@Service
public class ApiKeyService {

    private static final String PREFIX = "dpag_";
    private static final SecureRandom RANDOM = new SecureRandom();

    /** Gera uma nova chave aleatória. Retorna o valor em texto plano. */
    public String generatePlaintext() {
        byte[] bytes = new byte[20]; // 20 bytes → 40 hex chars
        RANDOM.nextBytes(bytes);
        return PREFIX + HexFormat.of().formatHex(bytes);
    }

    /** Calcula SHA-256 da chave. É o que se armazena no banco. */
    public String hash(String plaintextKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(plaintextKey.getBytes());
            return HexFormat.of().formatHex(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 não disponível", e);
        }
    }

    /** Retorna o prefixo visível para exibição (ex: dpag_a3f9b2...). */
    public String visiblePrefix(String plaintextKey) {
        return plaintextKey.substring(0, Math.min(14, plaintextKey.length())) + "...";
    }
}
