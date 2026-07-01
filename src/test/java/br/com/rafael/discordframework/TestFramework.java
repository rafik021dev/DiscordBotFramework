package br.com.rafael.discordframework;

import br.com.rafael.discordframework.annotations.Command;
import br.com.rafael.discordframework.annotations.Permission;
import br.com.rafael.discordframework.command.CommandContext;
import br.com.rafael.discordframework.command.CommandParser;
import br.com.rafael.discordframework.command.CommandRegistry;
import br.com.rafael.discordframework.command.RegisteredCommand;
import br.com.rafael.discordframework.cooldown.CooldownManager;
import br.com.rafael.discordframework.security.PermissionManager;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestFramework {

    @Test
    void verificaComandoeParametros() {
        CommandParser oParser = new CommandParser("!");

        CommandParser.ParsedCommand oComando = oParser.analisar("!ping teste 10").orElseThrow();

        assertEquals("ping", oComando.oNomeComando());
        assertEquals(List.of("teste", "10"), oComando.oArgumentos());
    }

    @Test
    void verificaCargo() {
        CommandRegistry oRegistro = new CommandRegistry();
        PermissionManager oPermissoes = new PermissionManager();
        oRegistro.registrarComandos(new ComandosTeste());
        RegisteredCommand oComando = oRegistro.buscarComando("admin").orElseThrow();
        CommandContext oContexto = new CommandContext("!admin", "admin", List.of(), oMensagem -> {});

        assertFalse(oPermissoes.temPermissao(oContexto, oComando));
    }

    @Test
    void tempoRestanteCooldown() {
        CooldownManager oCooldown = new CooldownManager(Clock.fixed(Instant.parse("2026-06-28T12:00:00Z"), ZoneOffset.UTC));

        oCooldown.registrar("ping:123", 30);

        assertTrue(oCooldown.estaEmCooldown("ping:123"));
        assertEquals(30, oCooldown.obterSegundosRestantes("ping:123"));
    }

    static class ComandosTeste {

        @Command(nome = "ping", apelidos = "p")
        void ping(CommandContext oContexto) {
            oContexto.responder("pong");
        }

        @Command(nome = "admin")
        @Permission(valor = "admin")
        void admin(CommandContext oContexto) {
            oContexto.responder("admin");
        }
    }
}
