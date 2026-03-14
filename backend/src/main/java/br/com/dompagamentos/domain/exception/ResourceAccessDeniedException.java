package br.com.dompagamentos.domain.exception;

/**
 * Lançada quando um merchant tenta acessar um recurso que pertence a outro merchant.
 * Retorna 403 no GlobalExceptionHandler.
 */
public class ResourceAccessDeniedException extends DomainException {

    public ResourceAccessDeniedException(String resource, Object resourceId) {
        super("Acesso negado: " + resource + " '" + resourceId + "' não pertence ao merchant informado.");
    }
}
