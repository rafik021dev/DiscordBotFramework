package br.com.rafael.discordframework.exception;

/**
 * Indica que dois métodos de comando tentaram usar o mesmo nome ou apelido.
 */
public class DuplicateCommandException extends FrameworkException {

    public DuplicateCommandException(String oMensagem) {
        super(oMensagem);
    }
}
