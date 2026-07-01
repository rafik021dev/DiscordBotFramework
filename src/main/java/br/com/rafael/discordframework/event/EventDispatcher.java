package br.com.rafael.discordframework.event;

import br.com.rafael.discordframework.command.CommandContext;
import br.com.rafael.discordframework.command.CommandExecutor;
import br.com.rafael.discordframework.command.CommandParser;
import br.com.rafael.discordframework.command.CommandRegistry;
import br.com.rafael.discordframework.command.RegisteredCommand;
import br.com.rafael.discordframework.cooldown.CooldownManager;
import br.com.rafael.discordframework.exception.CommandArgumentException;
import br.com.rafael.discordframework.exception.CommandExecutionException;
import br.com.rafael.discordframework.security.PermissionManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Despachante responsável por transformar eventos de mensagem do JDA em execuções de comandos do framework.
 *
 * <p>A classe ignora mensagens enviadas por bots, analisa mensagens com o prefixo configurado, localiza comandos
 * registrados, valida permissões, aplica cooldowns e trata falhas conhecidas de execução.</p>
 */
public class EventDispatcher extends ListenerAdapter {

    private static final Logger LOGGER = Logger.getLogger(EventDispatcher.class.getName());

    private final CommandRegistry commandRegistry;
    private final CommandParser commandParser;
    private final CommandExecutor commandExecutor;
    private final PermissionManager permissionManager;
    private final CooldownManager cooldownManager;

    /**
     * Cria um EventDispatcher com os componentes necessários para interpretar e executar comandos.
     *
     * @param oRegistroComandos usado para localizar os comandos pelo nome ou apelido
     * @param oAnalisadorComandos analisador responsável por extrair o comando e parâmetros do conteúdo da mensagem
     * @param oExecutorComandos executor pra invocar o comando registrado
     * @param oGerenciadorPermissoes gerenciador que valida as permissões antes da execução
     * @param oGerenciadorCooldown gerenciador que controla o intervalo de uso dos comandos
     * @throws NullPointerException se qualquer dependência recebida for {@code null}
     */
    public EventDispatcher(CommandRegistry oRegistroComandos, CommandParser oAnalisadorComandos, CommandExecutor oExecutorComandos, PermissionManager oGerenciadorPermissoes, CooldownManager oGerenciadorCooldown) {
        this.commandRegistry = Objects.requireNonNull(oRegistroComandos, "registroComandos");
        this.commandParser = Objects.requireNonNull(oAnalisadorComandos, "analisadorComandos");
        this.commandExecutor = Objects.requireNonNull(oExecutorComandos, "executorComandos");
        this.permissionManager = Objects.requireNonNull(oGerenciadorPermissoes, "gerenciadorPermissoes");
        this.cooldownManager = Objects.requireNonNull(oGerenciadorCooldown, "gerenciadorCooldown");
    }

    /**
     * Recebe mensagens do Discord e inicia o fluxo de execução quando identifica um comando registrado.
     *
     * Mensagens de bots são ignoradas, e mensagens sem o prefixo também.
     *
     * @param oEvento evento de mensagem recebido pelo JDA
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent oEvento) {
        if (oEvento.getAuthor().isBot()) {
            return;
        }
        String oConteudo = oEvento.getMessage().getContentRaw();
        commandParser.analisar(oConteudo)
                .flatMap(oComandoExtraido -> commandRegistry.buscarComando(oComandoExtraido.oNomeComando())
                        .map(oComando -> new CommandInvocation(oComando, oComandoExtraido)))
                .ifPresent(oInvocacao -> {
                    CommandParser.ParsedCommand oComandoExtraido = oInvocacao.oComandoExtraido();
                    CommandContext oContexto = new CommandContext(oEvento, oComandoExtraido.oConteudoOriginal(), oComandoExtraido.oNomeComando(), oComandoExtraido.oArgumentos());
                    executarComando(oInvocacao.oComando(), oContexto);
                });
    }

    /**
     * Executa um comando já com validação de permissão, cooldown e tratamento de erros.
     *
     * @param oComando comando que será executado
     * @param oContexto contexto com mensagem, autor, parâmetros e mecanismo de resposta
     */
    void executarComando(RegisteredCommand oComando, CommandContext oContexto) {
        if (!permissionManager.temPermissao(oContexto, oComando)) {
            oContexto.responder("Você não tem permissão para executar este comando.");
            return;
        }

        String oChaveCooldown = criarChaveCooldown(oContexto, oComando);
        if (estaEmCooldown(oComando, oChaveCooldown)) {
            long lSegundosRestantes = cooldownManager.obterSegundosRestantes(oChaveCooldown);
            oContexto.responder("Aguarde " + lSegundosRestantes + " segundo(s) para usar este comando novamente.");
            return;
        }
        try {
            commandExecutor.executar(oComando, oContexto);
            registrarCooldown(oComando, oChaveCooldown);
        } catch (CommandArgumentException oExcecao) {
            oContexto.responder(oExcecao.getMessage());
        } catch (CommandExecutionException oExcecao) {
            LOGGER.log(Level.SEVERE, "Falha ao executar comando: " + oComando.obterNome(), oExcecao);
            oContexto.responder("Ocorreu um erro ao executar este comando.");
        } catch (RuntimeException oExcecao) {
            LOGGER.log(Level.SEVERE, "Falha ao executar comando: " + oComando.obterNome(), oExcecao);
            oContexto.responder("Ocorreu um erro ao executar este comando.");
        }
    }

    /**
     * Verifica se o comando está em cooldown.
     *
     * @param oComando comando
     * @param oChaveCooldown chave do cooldown
     * @return {@code true} quando o comando ainda está em cooldown; {@code false} caso contrário
     */
    private boolean estaEmCooldown(RegisteredCommand oComando, String oChaveCooldown) {
        return oComando.obterCooldown()
                .filter(oCooldown -> oCooldown.segundos() > 0)
                .map(oCooldown -> cooldownManager.estaEmCooldown(oChaveCooldown))
                .orElse(false);
    }

    /**
     * Registra um novo cooldown para o comando executado quando ele possui cooldown configurado.
     *
     * @param oComando comando que acabou de ser executado
     * @param oChaveCooldown chave do cooldown
     */
    private void registrarCooldown(RegisteredCommand oComando, String oChaveCooldown) {
        oComando.obterCooldown()
                .filter(oCooldown -> oCooldown.segundos() > 0)
                .ifPresent(oCooldown -> cooldownManager.registrar(oChaveCooldown, oCooldown.segundos()));
    }

    /**
     * Cria a chave usada para controlar cooldown por comando e usuário.
     *
     * @param oContexto contexto da execução atual
     * @param oComando comando que está sendo executado
     * @return chave no formato {@code nomeDoComando:idDoUsuario}
     */
    private String criarChaveCooldown(CommandContext oContexto, RegisteredCommand oComando) {
        String oIdUsuario = oContexto.getAutor()
                .map(oUsuario -> oUsuario.getId())
                .orElse("sem-autor");
        return oComando.obterNome() + ":" + oIdUsuario;
    }

    /**
     * Agrupa o comando registrado e os dados extraídos da mensagem.
     *
     * @param oComando comando localizado no registro
     * @param oComandoExtraido dados analisados a partir do conteúdo da mensagem
     */
    private record CommandInvocation(RegisteredCommand oComando, CommandParser.ParsedCommand oComandoExtraido) {
    }
}
