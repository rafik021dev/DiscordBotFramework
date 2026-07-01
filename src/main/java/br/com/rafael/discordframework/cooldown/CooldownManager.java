package br.com.rafael.discordframework.cooldown;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Controla o cooldown dos comandos.
 */
public class CooldownManager {

    private final Clock clock;
    private final Map<String, Instant> expirations = new HashMap<>();

    /**
     * Constructor com o tempo padrão do sistema.
     */
    public CooldownManager() {
        this(Clock.systemUTC());
    }

    /**
     * Construtor com o tempo personalizado.
     * Útil para quando quiser definir o seu próprio tempo na aplicação.
     *
     * @param oRelogio relógio usado para calcular expirações de cooldown
     */
    public CooldownManager(Clock oRelogio) {
        this.clock = Objects.requireNonNull(oRelogio, "relogio");
    }

    /**
     * Registra uma expiração de cooldown para uma chave.
     *
     * @param oChave chave do cooldown
     * @param lSegundos duração em segundos
     */
    public void registrar(String oChave, long lSegundos) {
        expirations.put(oChave, Instant.now(clock).plusSeconds(lSegundos));
    }

    /**
     * Verifica se uma chave está em cooldown no momento.
     *
     * @param oChave chave do cooldown
     * @return true quando a chave ainda não expirou
     */
    public boolean estaEmCooldown(String oChave) {
        Instant oExpiracao = expirations.get(oChave);
        return oExpiracao != null && oExpiracao.isAfter(Instant.now(clock));
    }

    /**
     * Retorna quantos segundos faltam para uma chave sair do cooldown.
     *
     * @param oChave chave do cooldown
     * @return segundos restantes, ou zero quando nao existe cooldown ativo
     */
    public long obterSegundosRestantes(String oChave) {
        Instant oExpiracao = expirations.get(oChave);
        if (oExpiracao == null || !oExpiracao.isAfter(Instant.now(clock))) {
            return 0;
        }

        long lSegundosRestantes = Duration.between(Instant.now(clock), oExpiracao).toSeconds();
        return Math.max(1, lSegundosRestantes);
    }
}
