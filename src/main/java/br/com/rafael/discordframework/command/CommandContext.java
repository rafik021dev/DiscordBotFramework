package br.com.rafael.discordframework.command;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Fornece dados de execução e operações auxiliares para métodos de comando.
 */
public class CommandContext {

    private final MessageReceivedEvent evento;
    private final String conteudoComando;
    private final String nomeComando;
    private final List<String> parametros;
    private final Consumer<String> replyHandler;

    /**
     * Cria um contexto baseado em um evento de mensagem do JDA.
     *
     * @param oEvento evento de mensagem do JDA
     * @param oConteudoOriginal conteúdo original da mensagem
     * @param oNomeComando nome do comando extraído da mensagem
     * @param oArgumentos argumentos do comando
     */
    public CommandContext(MessageReceivedEvent oEvento, String oConteudoOriginal, String oNomeComando, List<String> oArgumentos) {
        this(Objects.requireNonNull(oEvento, "evento"), oConteudoOriginal, oNomeComando, oArgumentos,
                oMensagem -> oEvento.getChannel().sendMessage(oMensagem).queue());
    }

    /**
     * Cria um contexto com um manipulador de resposta personalizado, útil para testes e adaptadores.
     *
     * @param oConteudoOriginal conteúdo original da mensagem
     * @param oNomeComando nome do comando extraído da mensagem
     * @param oParametros argumentos do comando
     * @param oManipuladorResposta função de retorno usada por {@link #responder(String)}
     */
    public CommandContext(String oConteudoOriginal, String oNomeComando, List<String> oParametros, Consumer<String> oManipuladorResposta) {
        this(null, oConteudoOriginal, oNomeComando, oParametros, oManipuladorResposta);
    }

    private CommandContext(MessageReceivedEvent oEvento, String oConteudoOriginal, String oNomeComando, List<String> oArgumentos, Consumer<String> oManipuladorResposta) {
        this.evento = oEvento;
        this.conteudoComando = Objects.requireNonNull(oConteudoOriginal, "conteudoOriginal");
        this.nomeComando = Objects.requireNonNull(oNomeComando, "nomeComando");
        this.parametros = List.copyOf(Objects.requireNonNull(oArgumentos, "argumentos"));
        this.replyHandler = Objects.requireNonNull(oManipuladorResposta, "manipuladorResposta");
    }

    /**
     * Responde o comando executado no mesmo canal.
     *
     * @param oMensagem mensagem a ser enviada
     */
    public void responder(String oMensagem) {
        replyHandler.accept(Objects.requireNonNull(oMensagem, "mensagem"));
    }

    /**
     * Retorna o evento original do JDA quando este contexto veio do Discord.
     *
     * @return evento opcional do JDA
     */
    public Optional<MessageReceivedEvent> obterEvento() {
        return Optional.ofNullable(evento);
    }

    /**
     * Retorna o usuário do Discord que enviou a mensagem quando disponível.
     *
     * @return Retorna o nome do autor no chat
     */
    public Optional<User> getAutor() {
        return obterEvento().map(MessageReceivedEvent::getAuthor);
    }

    /**
     * Retorna o conteúdo original da mensagem.
     *
     * @return conteúdo original da mensagem
     */
    public String getConteudoOriginal() {
        return conteudoComando;
    }

    /**
     * Retorna o nome do comando extraído.
     *
     * @return nome do comando
     */
    public String getNomeComando() {
        return nomeComando;
    }

    /**
     * Retorna todos os parâmetros do comando.
     *
     * @return parâmetros do comando
     */
    public List<String> getParametros() {
        return parametros;
    }

    /**
     * Retorna um argumento pelo índice.
     *
     * @param iIndice índice do argumento
     * @return valor opcional do argumento
     */
    public Optional<String> getParametro(int iIndice) {
        if (iIndice < 0 || iIndice >= parametros.size()) {
            return Optional.empty();
        }

        return Optional.of(parametros.get(iIndice));
    }
}
