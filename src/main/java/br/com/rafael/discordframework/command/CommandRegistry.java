package br.com.rafael.discordframework.command;

import br.com.rafael.discordframework.annotations.Command;
import br.com.rafael.discordframework.exception.DuplicateCommandException;
import br.com.rafael.discordframework.exception.InvalidCommandDefinitionException;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Armazena comandos descobertos por reflexao.
 */
public class CommandRegistry {

    private final Map<String, RegisteredCommand> commandsByName = new LinkedHashMap<>();
    private final CommandParametersResolver argumentResolver = new CommandParametersResolver();

    /**
     * Registra todos os metodos anotados com {@link Command} em uma instancia de modulo.
     *
     * @param oModulo objeto que contem metodos de comando
     */
    public void registrarComandos(Object oModulo) {
        Objects.requireNonNull(oModulo, "modulo");

        for (Method oMetodo : oModulo.getClass().getDeclaredMethods()) {
            Command oComando = oMetodo.getAnnotation(Command.class);
            if (oComando != null) {
                registrarComando(oModulo, oMetodo, oComando);
            }
        }
    }

    private void registrarComando(Object oModulo, Method oMetodo, Command oComando) {
        validar(oMetodo, oComando);
        oMetodo.setAccessible(true);

        RegisteredCommand oComandoRegistrado = new RegisteredCommand(oModulo, oMetodo, oComando);
        adicionar(oComando.nome(), oComandoRegistrado);

        for (String oApelido : oComando.apelidos()) {
            adicionar(oApelido, oComandoRegistrado);
        }
    }

    private void validar(Method oMetodo, Command oComando) {
        if (oComando.nome().isBlank()) {
            throw new InvalidCommandDefinitionException("Nome do comando nao pode ser vazio: " + oMetodo.getName());
        }

        Class<?>[] oTiposParametros = oMetodo.getParameterTypes();
        if (oTiposParametros.length == 0 || oTiposParametros[0] != CommandContext.class) {
            throw new InvalidCommandDefinitionException(
                    "Metodo de comando deve receber CommandContext como primeiro parametro: " + oMetodo.getName()
            );
        }

        for (int iIndice = 1; iIndice < oTiposParametros.length; iIndice++) {
            Class<?> oTipoParametro = oTiposParametros[iIndice];
            if (!argumentResolver.suporta(oTipoParametro)) {
                throw new InvalidCommandDefinitionException(
                        "Tipo de parametro nao suportado em comando: " + oTipoParametro.getSimpleName() + " em " + oMetodo.getName()
                );
            }
        }

        if (oMetodo.getReturnType() != Void.TYPE) {
            throw new InvalidCommandDefinitionException(
                    "Metodo de comando deve retornar void: " + oMetodo.getName()
            );
        }
    }

    private void adicionar(String oNome, RegisteredCommand oComando) {
        String oChave = normalizar(oNome);
        if (commandsByName.containsKey(oChave)) {
            throw new DuplicateCommandException("Comando ja registrado: " + oNome);
        }
        commandsByName.put(oChave, oComando);
    }

    /**
     * Busca um comando pelo nome ou apelido.
     *
     * @param oNome nome ou apelido do comando
     * @return comando registrado quando encontrado
     */
    public Optional<RegisteredCommand> buscarComando(String oNome) {
        return Optional.ofNullable(commandsByName.get(normalizar(oNome)));
    }

    private String normalizar(String oNome) {
        if (oNome == null || oNome.isBlank()) {
            throw new IllegalArgumentException("nome do comando nao pode ser vazio");
        }

        return oNome.toLowerCase(Locale.ROOT);
    }
}
