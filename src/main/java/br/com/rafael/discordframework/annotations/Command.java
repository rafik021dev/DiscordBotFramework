package br.com.rafael.discordframework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca um método como comando a ser executado no Discord, que vai ser tratado pelo framework.
 *
 * Métodos de comando devem receber {@link br.com.rafael.discordframework.command.CommandContext} como primeiro
 * parâmetro e outros parâmetros suportados pelo framework.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    /**
     * Nome do comando usado depois do prefixo configurado.
     *
     * @return nome do comando
     */
    String nome();

    /**
     * Apelidos opcionais do comando.
     *
     * @return apelidos do comando
     */
    String[] apelidos() default {};

    /**
     * Descrição opcional para documentação ou comandos de ajuda.
     *
     * @return descrição do comando
     */
    String descricao() default "";
}
