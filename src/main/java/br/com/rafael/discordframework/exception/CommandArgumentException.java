package br.com.rafael.discordframework.exception;

/**
 * Indica que os argumentos informados pelo usuario nao batem com a assinatura do comando.
 */
public class CommandArgumentException extends FrameworkException {

    public CommandArgumentException(String oMensagem) {
        super(oMensagem);
    }

    public CommandArgumentException(String oMensagem, Throwable oCausa) {
        super(oMensagem, oCausa);
    }
}
