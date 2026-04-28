package ru.hexaend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-тесты для {@link SqlLoader}.
 *
 * @author Vasily Melnik
 */
@DisplayName("SqlLoader")
class SqlLoaderTest {

    @Test
    @DisplayName("getAsString: загружает существующий ресурс")
    void getAsString_existingResource_returnsContent() {
        final String sql = SqlLoader.getAsString("sql/contact/find-all.sql");

        assertNotNull(sql);
        assertFalse(sql.isBlank());
        assertTrue(sql.contains("SELECT"));
        assertTrue(sql.contains("FROM contacts c"));
    }

    @Test
    @DisplayName("getAsString: кэширует результат — повторный вызов возвращает ту же строку")
    void getAsString_cached_returnsSameInstance() {
        final String first = SqlLoader.getAsString("sql/contact/find-by-id.sql");
        final String second = SqlLoader.getAsString("sql/contact/find-by-id.sql");

        assertSame(first, second, "Результат должен быть закэширован (same reference)");
    }

    @Test
    @DisplayName("getAsString: выбрасывает IllegalArgumentException для несуществующего ресурса")
    void getAsString_missingResource_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> SqlLoader.getAsString("sql/nonexistent.sql"));
    }

    @Test
    @DisplayName("getAsString: загружает DDL-ресурс схемы")
    void getAsString_schemaResource_returnsDdl() {
        final String ddl = SqlLoader.getAsString("sql/schema/create-contacts.sql");

        assertNotNull(ddl);
        assertTrue(ddl.contains("CREATE TABLE"));
        assertTrue(ddl.contains("contacts"));
    }

    @Test
    @DisplayName("getAsString: содержимое не содержит ведущих/конечных пробелов")
    void getAsString_stripsTrailingWhitespace() {
        final String sql = SqlLoader.getAsString("sql/contact/delete-by-id.sql");

        assertFalse(sql.endsWith(" "), "Не должно быть конечных пробелов");
        assertFalse(sql.endsWith("\n"), "Не должно быть конечного перевода строки");
    }
}
