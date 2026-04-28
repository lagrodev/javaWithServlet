package ru.hexaend.domain.exeptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hexaend.domain.dto.HttpError;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-тесты для иерархии исключений приложения.
 *
 * @author Vasily Melnik
 */
@DisplayName("ApplicationException hierarchy")
class ApplicationExceptionTest {

    @Test
    @DisplayName("ValidationException: статус 400, HttpError.BAD_REQUEST")
    void validationException_correctHttpError() {
        final ValidationException ex = new ValidationException("test");

        assertEquals(400, ex.getStatus());
        assertEquals(HttpError.BAD_REQUEST, ex.getHttpError());
        assertEquals("test", ex.getMessage());
    }

    @Test
    @DisplayName("ContactNotFoundException: статус 404, HttpError.NOT_FOUND")
    void contactNotFoundException_correctHttpError() {
        final ContactNotFoundException ex = new ContactNotFoundException("not found");

        assertEquals(404, ex.getStatus());
        assertEquals(HttpError.NOT_FOUND, ex.getHttpError());
        assertEquals("not found", ex.getMessage());
    }

    @Test
    @DisplayName("DatabaseException без cause: статус 500, HttpError.INTERNAL_ERROR")
    void databaseException_withoutCause() {
        final DatabaseException ex = new DatabaseException("db error");

        assertEquals(500, ex.getStatus());
        assertEquals(HttpError.INTERNAL_ERROR, ex.getHttpError());
        assertEquals("db error", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("DatabaseException с cause: сохраняет причину")
    void databaseException_withCause() {
        final SQLException sqlEx = new SQLException("connection refused");
        final DatabaseException ex = new DatabaseException("db error", sqlEx);

        assertEquals(500, ex.getStatus());
        assertEquals(HttpError.INTERNAL_ERROR, ex.getHttpError());
        assertSame(sqlEx, ex.getCause());
    }

    @Test
    @DisplayName("Все исключения являются подтипом ApplicationException")
    void allExceptions_extendApplicationException() {
        assertTrue(ApplicationException.class.isAssignableFrom(ValidationException.class));
        assertTrue(ApplicationException.class.isAssignableFrom(ContactNotFoundException.class));
        assertTrue(ApplicationException.class.isAssignableFrom(DatabaseException.class));
    }

    @Test
    @DisplayName("Все исключения являются подтипом RuntimeException")
    void allExceptions_extendRuntimeException() {
        assertTrue(RuntimeException.class.isAssignableFrom(ValidationException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(ContactNotFoundException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(DatabaseException.class));
    }
}
