package br.com.rafael.discordframework.exception;

/**
 * Indica uma falha ao usar um comando registrado.
 */
public class CommandExecutionException extends FrameworkException {

    public CommandExecutionException(String oMensagem, Throwable oCausa) {
        super(oMensagem, oCausa);
    }
}
