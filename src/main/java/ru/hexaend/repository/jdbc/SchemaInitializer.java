package ru.hexaend.repository.jdbc;

import ru.hexaend.domain.exeptions.DatabaseException;
import ru.hexaend.util.SqlLoader;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Инициализатор схемы базы данных.
 *
 * <p>Создаёт таблицы {@code contacts} и {@code phone_numbers},
 * если они ещё не существуют. Вызывается один раз при старте
 * приложения из {@link ru.hexaend.rest.AppContextListener}.</p>
 *
 * @author Vasily Melnik
 */
public class SchemaInitializer {

    private static final Logger LOG = Logger.getLogger(SchemaInitializer.class.getName());

    private static final String SQL_CREATE_CONTACTS = SqlLoader.getAsString("sql/schema/create-contacts.sql");
    private static final String SQL_CREATE_PHONES = SqlLoader.getAsString("sql/schema/create-phone-numbers.sql");

    private final DataSourceProvider dataSourceProvider;

    /**
     * @param dataSourceProvider провайдер JDBC-соединений
     */
    public SchemaInitializer(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    /**
     * Выполняет DDL-скрипты для создания таблиц.
     *
     * @throws DatabaseException если не удалось выполнить DDL
     */
    public void initialize() {
        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(SQL_CREATE_CONTACTS);
            stmt.execute(SQL_CREATE_PHONES);
            LOG.info("Схема БД инициализирована успешно");
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Ошибка инициализации схемы БД", e);
            throw new DatabaseException("Ошибка инициализации схемы БД", e);
        }
    }
}
