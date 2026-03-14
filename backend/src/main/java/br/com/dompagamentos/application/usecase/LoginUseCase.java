package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.LoginInputPort;
import br.com.dompagamentos.application.ports.output.MerchantUserRepositoryOutputPort;
import br.com.dompagamentos.domain.exception.InvalidCredentialsException;
import br.com.dompagamentos.domain.model.MerchantUser;
import br.com.dompagamentos.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoginUseCase implements LoginInputPort {

    private final MerchantUserRepositoryOutputPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginUseCase(MerchantUserRepositoryOutputPort userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResult execute(Command command) {
        MerchantUser user = userRepository.findByEmail(command.email())
                .filter(MerchantUser::isActive)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generate(user.getId(), user.getMerchantId());
        long expiresAt = jwtService.extractExpiration(token);

        return new LoginResult(token, user.getMerchantId(), expiresAt);
    }
}
