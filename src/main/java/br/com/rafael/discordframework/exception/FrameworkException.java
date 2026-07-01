package br.com.rafael.discordframework.exception;

/**
 * Exceção base não checada para caso o framework falhe.
 */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String oMensagem) {
        super(oMensagem);
    }

    public FrameworkException(String oMensagem, Throwable oCausa) {
        super(oMensagem, oCausa);
    }
}
