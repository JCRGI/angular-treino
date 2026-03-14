package br.com.dompagamentos.application.usecase;

import br.com.dompagamentos.application.ports.input.RegisterUserInputPort;
import br.com.dompagamentos.application.ports.output.MerchantRepositoryOutputPort;
import br.com.dompagamentos.application.ports.output.MerchantUserRepositoryOutputPort;
import br.com.dompagamentos.domain.exception.MerchantNotFoundException;
import br.com.dompagamentos.domain.exception.UserAlreadyExistsException;
import br.com.dompagamentos.domain.model.MerchantUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUserUseCase implements RegisterUserInputPort {

    private final MerchantRepositoryOutputPort merchantRepository;
    private final MerchantUserRepositoryOutputPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(MerchantRepositoryOutputPort merchantRepository,
                               MerchantUserRepositoryOutputPort userRepository,
                               PasswordEncoder passwordEncoder) {
        this.merchantRepository = merchantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(Command command) {
        // Valida que o merchant existe
        merchantRepository.findById(command.merchantId())
                .orElseThrow(() -> new MerchantNotFoundException(command.merchantId()));

        // E-mail deve ser único
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        String passwordHash = passwordEncoder.encode(command.password());
        MerchantUser user = MerchantUser.create(command.merchantId(), command.email(), passwordHash);
        userRepository.save(user);
    }
}
