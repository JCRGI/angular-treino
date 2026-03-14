package br.com.dompagamentos.application.ports.input;

import java.util.UUID;

public interface RegisterUserInputPort {

    void execute(Command command);

    record Command(UUID merchantId, String email, String password) {}
}
