package br.com.rafael.discordframework.exception;

/**
 * Indica que um método anotado como comando não segue a assinatura exigida.
 */
public class InvalidCommandDefinitionException extends FrameworkException {

    public InvalidCommandDefinitionException(String oMensagem) {
        super(oMensagem);
    }
}
