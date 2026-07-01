package br.com.rafael.discordframework.core;

import br.com.rafael.discordframework.command.CommandRegistry;
import net.dv8tion.jda.api.JDA;

import java.util.Objects;

/**
 * Objeto principal de execução do bot criado pelo framework.
 *
 * Esta classe representa o bot depois que a integração com o JDA já foi construída. Ela mantém a instância do
 * {@link JDA}, permite registrar novos comandos em tempo de execução.
 */
public class DiscordBot {

    private final JDA jda;
    private final CommandRegistry commandRegistry;

    DiscordBot(JDA oJda, CommandRegistry oRegistroComandos) {
        this.jda = Objects.requireNonNull(oJda, "jda");
        this.commandRegistry = Objects.requireNonNull(oRegistroComandos, "registroComandos");
    }

    /**
     * Registra métodos de comando depois que o bot já está rodando.
     *
     * Use este método quando precisar adicionar novos módulos de comandos após a chamada de {@link BotBuilder#construir()}. Os comandos são inseridos diretamente no registro usado pelo despachante de
     * eventos já conectado ao JDA.
     *
     * @param oModulo módulo que contém métodos anotados
     */
    public void registrarComandos(Object oModulo) {
        commandRegistry.registrarComandos(oModulo);
    }

    /**
     * Encerra o JDA.
     */
    public void encerrar() {
        jda.shutdown();
    }

    /**
     * Retorna o JDA.
     *
     * @return JDA
     */
    public JDA obterJda() {
        return jda;
    }
}
