package br.com.rafael.discordframework.exception;

/**
 * Indica uma configuração inválida de inicialização do framework.
 */
public class FrameworkInitializationException extends FrameworkException {

    public FrameworkInitializationException(String oMensagem) {
        super(oMensagem);
    }
}
