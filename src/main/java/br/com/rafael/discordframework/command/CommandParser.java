package br.com.rafael.discordframework.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Analisa os comando do framework.
 */
public class CommandParser {

    private final String prefix;

    /**
     * Cria um analisador para um prefixo de comando.
     *
     * @param oPrefixo prefixo do comando
     */
    public CommandParser(String oPrefixo) {
        if (oPrefixo == null || oPrefixo.isBlank()) {
            throw new IllegalArgumentException("prefixo não pode ser vazio");
        }

        this.prefix = oPrefixo;
    }

    /**
     * Analisa o conteúdo inteiro da mensagem.
     *
     * @param oConteudo conteúdo da mensagem
     * @return Retorna o comando analisado, mas só se ele começar com o prefixo definido, se não ele ignora.
     */
    public Optional<ParsedCommand> analisar(String oConteudo) {
        Objects.requireNonNull(oConteudo, "conteudo");
        if (!oConteudo.startsWith(prefix)) {
            return Optional.empty();
        }
        String oCorpo = oConteudo.substring(prefix.length()).trim();
        if (oCorpo.isEmpty()) {
            return Optional.empty();
        }

        List<String> oPartes = separarPartes(oCorpo);
        if (oPartes.isEmpty()) {
            return Optional.empty();
        }

        String oNomeComando = oPartes.get(0).toLowerCase(Locale.ROOT);
        List<String> oArgumentos = oPartes.size() == 1 ? List.of() : List.copyOf(oPartes.subList(1, oPartes.size()));

        return Optional.of(new ParsedCommand(oNomeComando, oArgumentos, oConteudo));
    }

    private List<String> separarPartes(String oCorpo) {
        List<String> oPartes = new ArrayList<>();
        StringBuilder oParteAtual = new StringBuilder();
        boolean bDentroDeAspas = false;
        boolean bEscapando = false;

        for (int iIndice = 0; iIndice < oCorpo.length(); iIndice++) {
            char cCaractere = oCorpo.charAt(iIndice);

            if (bEscapando) {
                oParteAtual.append(cCaractere);
                bEscapando = false;
                continue;
            }

            if (cCaractere == '\\') {
                bEscapando = true;
                continue;
            }

            if (cCaractere == '"') {
                bDentroDeAspas = !bDentroDeAspas;
                continue;
            }

            if (Character.isWhitespace(cCaractere) && !bDentroDeAspas) {
                adicionarParte(oPartes, oParteAtual);
                continue;
            }

            oParteAtual.append(cCaractere);
        }

        if (bEscapando) {
            oParteAtual.append('\\');
        }

        adicionarParte(oPartes, oParteAtual);
        return oPartes;
    }

    private void adicionarParte(List<String> oPartes, StringBuilder oParteAtual) {
        if (!oParteAtual.isEmpty()) {
            oPartes.add(oParteAtual.toString());
            oParteAtual.setLength(0);
        }
    }

    /**
     * Retorna o prefixo do comando.
     *
     * @return prefixo de comando
     */
    public String getPrefixo() {
        return prefix;
    }

    /**
     * Dados do comando.
     *
     * @param oNomeComando nome do comando extraído
     * @param oArgumentos argumentos extraídos do comando
     * @param oConteudoOriginal conteúdo original da mensagem
     */
    public record ParsedCommand(String oNomeComando, List<String> oArgumentos, String oConteudoOriginal) {

        public ParsedCommand {
            Objects.requireNonNull(oNomeComando, "nomeComando");
            oArgumentos = List.copyOf(Objects.requireNonNull(oArgumentos, "argumentos"));
            Objects.requireNonNull(oConteudoOriginal, "conteudoOriginal");
        }
    }
}
