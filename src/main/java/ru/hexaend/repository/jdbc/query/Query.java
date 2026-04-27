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
 * <p>SQL можно указать двумя способами:
 * <ul>
 *   <li>Инлайн: {@code @Query("SELECT * FROM contacts")}</li>
 *   <li>Из ресурса: {@code @Query(fromResource = "sql/contact/find-all.sql")}</li>
 * </ul>
 * Если указан {@link #fromResource}, он имеет приоритет над {@link #value()}.</p>
 *
 * @author Vasily Melnik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Query {

    /**
     * SQL-запрос, который будет выполнен при вызове метода.
     */
    String value() default "";

    /**
     * Путь к SQL-файлу в classpath (например, {@code "sql/contact/find-all.sql"}).
     *
     * <p>Если не пуст — SQL загружается через {@link ru.hexaend.util.SqlLoader}
     * и имеет приоритет над {@link #value()}.</p>
     */
    String fromResource() default "";
}
