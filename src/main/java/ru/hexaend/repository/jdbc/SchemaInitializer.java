package ru.hexaend.repository.jdbc;

import ru.hexaend.ex.custom.DatabaseException;

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
        String createContacts = """
                CREATE TABLE IF NOT EXISTS contacts (
                    id         VARCHAR(36) PRIMARY KEY,   
                    first_name VARCHAR(100) NOT NULL,     
                    last_name  VARCHAR(100) NOT NULL,     
                    created_at TIMESTAMP NOT NULL DEFAULT now(), 
                    updated_at TIMESTAMP NOT NULL DEFAULT now()  ч
                )
                """;

        String createPhones = """
                CREATE TABLE IF NOT EXISTS phone_numbers (
                    id         SERIAL PRIMARY KEY,
                    contact_id VARCHAR(36) NOT NULL,
                    phone      VARCHAR(50) NOT NULL,
                    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE
                )
                """;

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createContacts);
            stmt.execute(createPhones);
            LOG.info("Схема БД инициализирована успешно");
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Ошибка инициализации схемы БД", e);
            throw new DatabaseException("Ошибка инициализации схемы БД", e);
        }
    }
}
