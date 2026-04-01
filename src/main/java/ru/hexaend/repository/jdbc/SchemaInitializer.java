package ru.hexaend.repository.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class SchemaInitializer
{
    private final DataSourceProvider dataSourceProvider;

    public SchemaInitializer(DataSourceProvider dataSourceProvider)
    {
        this.dataSourceProvider = dataSourceProvider;
    }

    public void initialize()
    {
        String createContacts = """
                CREATE TABLE IF NOT EXISTS contacts (
                    id         VARCHAR(36) PRIMARY KEY,
                    first_name VARCHAR(100) NOT NULL,
                    last_name  VARCHAR(100) NOT NULL
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
             Statement stmt = conn.createStatement())
        {
            stmt.execute(createContacts);
            stmt.execute(createPhones);
        } catch (SQLException e)
        {
            throw new RuntimeException("Ошибка инициализации схемы БД", e);
        }
    }
}
