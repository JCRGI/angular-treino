package br.com.dompagamentos.domain.exception;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super("E-mail ou senha inválidos.");
    }
}
