package br.com.rafael.discordframework.core;

import br.com.rafael.discordframework.command.CommandExecutor;
import br.com.rafael.discordframework.command.CommandParser;
import br.com.rafael.discordframework.command.CommandRegistry;
import br.com.rafael.discordframework.cooldown.CooldownManager;
import br.com.rafael.discordframework.event.EventDispatcher;
import br.com.rafael.discordframework.exception.FrameworkInitializationException;
import br.com.rafael.discordframework.security.PermissionManager;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.List;

/**
 * Configura e inicia um bot Discord baseado em JDA.
 */
public class BotBuilder {

    private String token;
    private String prefix = "!";
    private final List<Object> commandModules = new ArrayList<>();

    private BotBuilder() {}

    /**
     * Cria um novo construtor de bot.
     *
     * @return instância do construtor
     */
    public static BotBuilder criar() {
        return new BotBuilder();
    }

    /**
     * Configura o token do bot Discord.
     *
     * @param sToken token do bot Discord
     * @return this
     */
    public BotBuilder setToken(String sToken) {
        this.token = sToken;
        return this;
    }

    /**
     * Configura o prefixo de comando.
     *
     * @param sPrefixo prefixo de comando
     * @return this
     */
    public BotBuilder setPrefixo(String sPrefixo) {
        this.prefix = sPrefixo;
        return this;
    }

    /**
     * Registra um módulo de comandos antes de iniciar o bot.
     *
     * @param oModulo objeto que contém métodos anotados como comandos
     * @return this
     */
    public BotBuilder registrarComandos(Object oModulo) {
        this.commandModules.add(oModulo);
        return this;
    }

    /**
     * Inicia o JDA e retorna o objeto de execução do bot.
     *
     * @return bot Discord inicializado
     */
    public DiscordBot construir() {
        if (token == null || token.isBlank()) {
            throw new FrameworkInitializationException("Token do Discord é obrigatório");
        }

        CommandRegistry oRegistroComandos = new CommandRegistry();
        commandModules.forEach(oRegistroComandos::registrarComandos);

        CommandParser oAnalisadorComandos = new CommandParser(prefix);
        CommandExecutor oExecutorComandos = new CommandExecutor();
        PermissionManager oGerenciadorPermissoes = new PermissionManager();
        CooldownManager oGerenciadorCooldown = new CooldownManager();
        EventDispatcher oEventDispatcher = new EventDispatcher(oRegistroComandos, oAnalisadorComandos, oExecutorComandos, oGerenciadorPermissoes, oGerenciadorCooldown);

        return new DiscordBot(
                JDABuilder.createDefault(token)
                        .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                        .addEventListeners(oEventDispatcher)
                        .build(),
                oRegistroComandos
        );
    }
}
