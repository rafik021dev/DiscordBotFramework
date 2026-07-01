package br.com.rafael.discordframework.command;

import java.util.Objects;

/**
 * Executa comandos registrados no framework.
 */
public class CommandExecutor {

    /**
     * Executa um comando registrado com o contexto informado.
     *
     * @param oComando comando registrado
     * @param oContexto contexto do comando
     */
    public void executar(RegisteredCommand oComando, CommandContext oContexto) {
        Objects.requireNonNull(oComando, "comando");
        Objects.requireNonNull(oContexto, "contexto");

        oComando.invocar(oContexto);
    }
}
