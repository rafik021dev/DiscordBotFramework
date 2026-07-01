package br.com.rafael.discordframework.security;

import br.com.rafael.discordframework.annotations.Permission;
import br.com.rafael.discordframework.command.CommandContext;
import br.com.rafael.discordframework.command.RegisteredCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Objects;

/**
 * Ponto central para verificacoes de permissao de comandos.
 */
public class PermissionManager {

    /**
     * Verifica se o comando pode ser executado.
     *
     * Comandos sem {@link Permission} sao liberados. Quando o comando declara uma permissao,
     * o valor da annotation deve bater com o nome, ID ou mencao de um cargo do autor.
     *
     * @param oContexto contexto do comando
     * @param oComando comando registrado
     * @return true quando a execucao e permitida
     */
    public boolean temPermissao(CommandContext oContexto, RegisteredCommand oComando) {
        Objects.requireNonNull(oContexto, "contexto");
        Objects.requireNonNull(oComando, "comando");

        return oComando.obterPermissao()
                .map(oPermissao -> temCargo(oContexto, oPermissao.valor()))
                .orElse(true);
    }

    /**
     * Verifica se o usuário tem o cargo exigido.
     *
     * @param oContexto contexto do comando
     * @param oCargoExigido cargo exigido
     * @return bool
     */
    private boolean temCargo(CommandContext oContexto, String oCargoExigido) {
        String oCargoNormalizado = Objects.requireNonNull(oCargoExigido, "cargoExigido").trim();
        if (oCargoNormalizado.isEmpty()) {
            return false;
        }

        return oContexto.obterEvento()
                .map(oEvento -> oEvento.getMember())
                .map(oMembro -> temCargo(oMembro, oCargoNormalizado))
                .orElse(false);
    }

    /**
     * Verifica se o usuário tem o cargo passado por parâmetro.
     *
     * @param oMembro usuário
     * @param oCargoExigido cargo a ser verificado
     * @return bool
     */
    private boolean temCargo(Member oMembro, String oCargoExigido) {
        return oMembro.getRoles().stream().anyMatch(oCargo -> correspondeAoCargo(oCargo, oCargoExigido));
    }

    /**
     * Verifica se os cargos passados por parâmetros são iguais.
     * Verifica pelo nome ou ID ou @.
     *
     * @param oCargo cargo 1
     * @param oCargoExigido cargo 2
     * @return bool
     */
    private boolean correspondeAoCargo(Role oCargo, String oCargoExigido) {
        return oCargo.getName().equalsIgnoreCase(oCargoExigido)
                || oCargo.getId().equals(oCargoExigido)
                || oCargo.getAsMention().equals(oCargoExigido);
    }
}
