package br.com.rafael.discordframework.command;

import br.com.rafael.discordframework.exception.CommandArgumentException;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

/**
 * Converte os parâmetros da mensagem para os parâmetros do método.
 */
class CommandParametersResolver {

    Object[] resolver(Method oMetodo, CommandContext oContexto) {
        Class<?>[] oTipos = oMetodo.getParameterTypes();
        Object[] oValores = new Object[oTipos.length];
        oValores[0] = oContexto;
        if (oTipos.length == 1) {
            return oValores;
        }

        List<String> oArgumentos = oContexto.getParametros();
        int iParametrosDeclarados = oTipos.length - 1;

        if (oArgumentos.size() < iParametrosDeclarados) {
            throw new CommandArgumentException("Parâmetros insuficientes. Esperado " + iParametrosDeclarados + ", recebido " + oArgumentos.size() + ".");
        }

        int iIndiceArgumento = 0;
        for (int iIndiceParametro = 1; iIndiceParametro < oTipos.length; iIndiceParametro++) {
            Class<?> oTipo = oTipos[iIndiceParametro];
            String oValor = oArgumentos.get(iIndiceArgumento++);
            oValores[iIndiceParametro] = converter(oValor, oTipo);
        }

        return oValores;
    }

    boolean suporta(Class<?> oTipo) {
        return oTipo == String.class
                || oTipo == Integer.TYPE
                || oTipo == Integer.class
                || oTipo == Long.TYPE
                || oTipo == Long.class
                || oTipo == Double.TYPE
                || oTipo == Double.class
                || oTipo == Boolean.TYPE
                || oTipo == Boolean.class
                || oTipo == BigDecimal.class;
    }

    private Object converter(String oValor, Class<?> oTipo) {
        if (oTipo == String.class) {
            return oValor;
        }

        try {
            if (oTipo == Integer.TYPE || oTipo == Integer.class) {
                return Integer.parseInt(oValor);
            }
            if (oTipo == Long.TYPE || oTipo == Long.class) {
                return Long.parseLong(oValor);
            }
            if (oTipo == Double.TYPE || oTipo == Double.class) {
                return Double.parseDouble(oValor);
            }
            if (oTipo == BigDecimal.class) {
                return new BigDecimal(oValor);
            }
            if (oTipo == Boolean.TYPE || oTipo == Boolean.class) {
                return converterBoolean(oValor);
            }
        } catch (NumberFormatException oExcecao) {
            throw new CommandArgumentException("Parâmetro '" + oValor + "' não é válido para o tipo " + oTipo.getSimpleName() + ".", oExcecao);
        }

        throw new CommandArgumentException("Tipo de parâmetro não suportado: " + oTipo.getSimpleName() + ".");
    }

    private boolean converterBoolean(String oValor) {
        return switch (oValor.toLowerCase()) {
            case "true", "sim", "yes" -> true;
            case "false", "nao", "no" -> false;
            default -> throw new CommandArgumentException("Parâmetro '" + oValor + "' não é válido para o tipo boolean.");
        };
    }
}
