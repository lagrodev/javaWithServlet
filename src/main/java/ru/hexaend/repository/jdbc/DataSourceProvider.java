package ru.hexaend.repository.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Провайдер JDBC-соединений.
 *
 * <p><b>Контракт:</b> вызывающая сторона обязана самостоятельно закрыть
 * полученное соединение (например, через try-with-resources).</p>
 *
 * @author Vasily Melnik
 */
public interface DataSourceProvider {

    /**
     * Возвращает открытое JDBC-соединение.
     *
     * @return новое или повторно используемое соединение
     * @throws SQLException если не удалось получить соединение
     */
    Connection getConnection() throws SQLException;
}
