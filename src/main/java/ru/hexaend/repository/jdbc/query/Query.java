package ru.hexaend.repository.jdbc.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для привязки SQL-запроса к методу репозитория.
 *
 * <p>Используется совместно с {@link Mapper} и обрабатывается
 * в {@link RepositoryInvocationHandler} через динамический прокси.</p>
 *
 * @author Vasily Melnik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Query {

    /**
     * SQL-запрос, который будет выполнен при вызове метода.
     */
    String value();
}
