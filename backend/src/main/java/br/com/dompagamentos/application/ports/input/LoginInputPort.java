package br.com.dompagamentos.application.ports.input;

import java.util.UUID;

public interface LoginInputPort {

    LoginResult execute(Command command);

    record Command(String email, String password) {}

    record LoginResult(String token, UUID merchantId, long expiresAt) {}
}
