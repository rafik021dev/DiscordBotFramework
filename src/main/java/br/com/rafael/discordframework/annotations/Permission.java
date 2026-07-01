package br.com.rafael.discordframework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define a permissão necessária para executar um comando.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permission {

    /**
     * Identificador da permissão exigida pelo comando.
     *
     * @return identificador da permissão
     */
    String valor();
}
