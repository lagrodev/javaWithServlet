package ru.hexaend.repository.jdbc.query;

import ru.hexaend.domain.mapper.RowMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для указания класса {@link RowMapper}, используемого
 * при маппинге результата SQL-запроса в объект.
 *
 * <p>Используется совместно с {@link Query} и обрабатывается
 * в {@link RepositoryInvocationHandler}.</p>
 *
 * @author Vasily Melnik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Mapper {

    /**
     * Класс маппера, преобразующего строку {@code ResultSet} в объект.
     */
    Class<? extends RowMapper<?>> value();
}
