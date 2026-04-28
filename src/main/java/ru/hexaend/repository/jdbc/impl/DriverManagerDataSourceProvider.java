package ru.hexaend.repository.jdbc.impl;

import ru.hexaend.repository.jdbc.DataSourceProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Реализация {@link DataSourceProvider}, создающая новое JDBC-соединение через {@link
 * DriverManager} при каждом вызове {@link #getConnection()}.
 *
 * <p>Подходит для простых приложений и тестов. В продуктивной среде рекомендуется заменить на
 * реализацию с пулом соединений (HikariCP, DBCP).
 *
 * <p><b>Контракт:</b> вызывающая сторона обязана самостоятельно закрыть полученное соединение после
 * использования.
 *
 * @author Vasily Melnik
 */
public class DriverManagerDataSourceProvider implements DataSourceProvider {

    private final String url;
    private final String username;
    private final String password;

    /**
     * Создаёт провайдер с указанными параметрами подключения.
     *
     * @param url      JDBC URL базы данных, например {@code jdbc:postgresql://localhost:5432/phonebook}
     * @param username имя пользователя БД
     * @param password пароль пользователя БД
     */
    public DriverManagerDataSourceProvider(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Создаёт провайдер только с JDBC URL (без аутентификации).
     *
     * @param url JDBC URL базы данных
     */
    public DriverManagerDataSourceProvider(String url) {
        this(url, null, null);
    }

    /**
     * Открывает и возвращает новое JDBC-соединение.
     *
     * <p>Если {@code username} задан — подключение выполняется с аутентификацией, иначе — без неё.
     *
     * @return новое открытое соединение с БД
     * @throws SQLException если не удалось установить соединение
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (username != null) {
            return DriverManager.getConnection(url, username, password);
        }
        return DriverManager.getConnection(url);
  }
}
