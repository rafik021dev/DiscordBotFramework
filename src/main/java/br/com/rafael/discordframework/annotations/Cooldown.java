package br.com.rafael.discordframework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define um intervalo de cooldown para um método de comando.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cooldown {

    /**
     * Duração do cooldown em segundos.
     *
     * @return duração do cooldown
     */
    long segundos();
}
