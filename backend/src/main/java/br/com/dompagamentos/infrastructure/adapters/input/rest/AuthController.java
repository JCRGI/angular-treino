package br.com.dompagamentos.infrastructure.adapters.input.rest;

import br.com.dompagamentos.application.ports.input.LoginInputPort;
import br.com.dompagamentos.application.ports.input.RegisterUserInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Autenticação por login/senha — retorna JWT para uso no dashboard.
 *
 * Fluxo:
 *  1. POST /api/v1/auth/register  → cria credenciais para o merchant
 *  2. POST /api/v1/auth/login     → retorna JWT
 *  3. Usa Bearer <token> nos requests seguintes
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Login e registro de usuários do dashboard")
public class AuthController {

    private final RegisterUserInputPort registerUser;
    private final LoginInputPort login;

    public AuthController(RegisterUserInputPort registerUser, LoginInputPort login) {
        this.registerUser = registerUser;
        this.login = login;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário",
               description = "Cria credenciais de acesso ao dashboard para um merchant já cadastrado.")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        registerUser.execute(new RegisterUserInputPort.Command(
                request.merchantId(), request.email(), request.password()
        ));
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login",
               description = "Autentica com e-mail e senha. Retorna JWT válido por 24h.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginInputPort.LoginResult result = login.execute(
                new LoginInputPort.Command(request.email(), request.password())
        );
        return ResponseEntity.ok(new LoginResponse(
                result.token(),
                result.merchantId(),
                Instant.ofEpochMilli(result.expiresAt()).toString()
        ));
    }

    // ===== Request / Response records =====

    record RegisterRequest(
            @NotNull UUID merchantId,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres") String password
    ) {}

    record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    record LoginResponse(String token, UUID merchantId, String expiresAt) {}
}
