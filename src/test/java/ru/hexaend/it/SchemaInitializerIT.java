package ru.hexaend.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.hexaend.repository.jdbc.SchemaInitializer;

import java.sql.Connection;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционный тест для {@link SchemaInitializer}.
 *
 * <p>Проверяет, что DDL-скрипты корректно создают таблицы в реальной PostgreSQL через
 * Testcontainers, и что повторный вызов {@code initialize()} идемпотентен (IF NOT EXISTS). Слой:
 * репозиторий (DDL). Контейнер: PostgreSQL.
 *
 * @author Vasily Melnik
 */
@DisplayName("SchemaInitializer — интеграционный тест")
class SchemaInitializerIT extends AbstractDatabaseIT {

    /**
     * Проверяет существование таблицы по имени через {@code information_schema}.
     *
     * @param tableName имя таблицы
     * @return {@code true}, если таблица существует
     */
    private boolean tableExists(String tableName) throws Exception {
        try (final Connection conn = dataSourceProvider.getConnection()) {
            try (final ResultSet rs =
                         conn
                                 .createStatement()
                                 .executeQuery(
                                         "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = '"
                                                 + tableName
                                                 + "'")) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    @Nested
    @DisplayName("initialize()")
    class Initialize {

        /**
         * Проверяет, что после {@code initialize()} таблица {@code contacts} существует в БД.
         *
         * <p>Схема уже создана в {@link AbstractDatabaseIT#initSchema()}, поэтому достаточно
         * проверить наличие таблицы.
         */
        @Test
        @DisplayName("таблица contacts существует после initialize()")
        void contactsTableExists() throws Exception {
            assertTrue(tableExists("contacts"), "Таблица contacts должна существовать");
        }

        /**
         * Проверяет, что после {@code initialize()} таблица {@code phone_numbers} существует в БД.
         *
         * <p>Схема уже создана в {@link AbstractDatabaseIT#initSchema()}, поэтому достаточно
         * проверить наличие таблицы.
         */
        @Test
        @DisplayName("таблица phone_numbers существует после initialize()")
        void phoneNumbersTableExists() throws Exception {
            assertTrue(tableExists("phone_numbers"), "Таблица phone_numbers должна существовать");
        }

        /**
         * Проверяет идемпотентность повторного вызова {@code initialize()}.
         *
         * <p>DDL-скрипты используют {@code IF NOT EXISTS}, поэтому повторный вызов не должен
         * выбрасывать исключение и не должен удалять существующие данные.
         */
        @Test
        @DisplayName("повторный вызов initialize() — идемпотентен (IF NOT EXISTS)")
        void repeatedInitialize_isIdempotent() {
            // Повторная инициализация не должна выбрасывать исключение
            assertDoesNotThrow(() -> new SchemaInitializer(dataSourceProvider).initialize());

            // Таблицы по-прежнему существуют
            assertDoesNotThrow(
                    () -> {
                        assertTrue(tableExists("contacts"));
                        assertTrue(tableExists("phone_numbers"));
                    });
        }
    }
}
