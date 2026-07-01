package br.com.rafael.discordframework.command;

import br.com.rafael.discordframework.annotations.Command;
import br.com.rafael.discordframework.annotations.Cooldown;
import br.com.rafael.discordframework.annotations.Permission;
import br.com.rafael.discordframework.exception.CommandExecutionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Representa um método de comando descoberto por reflexão.
 */
public class RegisteredCommand {

    private final String name;
    private final Object target;
    private final Method method;
    private final Cooldown cooldown;
    private final Permission permission;
    private final CommandParametersResolver argumentResolver = new CommandParametersResolver();

    /**
     * Cria um comando registrado.
     *
     * @param oAlvo objeto que contém o método de comando
     * @param oMetodo método de comando
     * @param oComando annotation de comando
     */
    public RegisteredCommand(Object oAlvo, Method oMetodo, Command oComando) {
        this.target = Objects.requireNonNull(oAlvo, "alvo");
        this.method = Objects.requireNonNull(oMetodo, "metodo");
        this.name = Objects.requireNonNull(oComando, "comando").nome().toLowerCase(Locale.ROOT);
        this.cooldown = oMetodo.getAnnotation(Cooldown.class);
        this.permission = oMetodo.getAnnotation(Permission.class);
    }

    /**
     * Invoca o método de comando.
     *
     * @param oContexto contexto do comando
     */
    public void invocar(CommandContext oContexto) {
        try {
            method.invoke(target, argumentResolver.resolver(method, oContexto));
        } catch (IllegalAccessException oExcecao) {
            throw new CommandExecutionException("Não foi possível acessar o método de comando: " + method.getName(), oExcecao);
        } catch (InvocationTargetException oExcecao) {
            throw new CommandExecutionException("Falha ao executar comando: " + name, oExcecao.getCause());
        }
    }

    public String obterNome() {
        return name;
    }

    public Optional<Cooldown> obterCooldown() {
        return Optional.ofNullable(cooldown);
    }

    public Optional<Permission> obterPermissao() {
        return Optional.ofNullable(permission);
    }
}
