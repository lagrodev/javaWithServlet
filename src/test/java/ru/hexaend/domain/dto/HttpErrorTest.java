package ru.hexaend.domain.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Unit-тесты для {@link HttpError}.
 *
 * @author Vasily Melnik
 */
@DisplayName("HttpError")
class HttpErrorTest {

    @Test
    @DisplayName("BAD_REQUEST: статус 400")
    void badRequest_statusCode() {
        assertEquals(400, HttpError.BAD_REQUEST.statusCode());
    }

    @Test
    @DisplayName("BAD_REQUEST: код 'Bad Request'")
    void badRequest_code() {
        assertEquals("Bad Request", HttpError.BAD_REQUEST.code());
    }

    @Test
    @DisplayName("NOT_FOUND: статус 404")
    void notFound_statusCode() {
        assertEquals(404, HttpError.NOT_FOUND.statusCode());
    }

    @Test
    @DisplayName("NOT_FOUND: код 'Not Found'")
    void notFound_code() {
        assertEquals("Not Found", HttpError.NOT_FOUND.code());
    }

    @Test
    @DisplayName("CONFLICT: статус 409")
    void conflict_statusCode() {
        assertEquals(409, HttpError.CONFLICT.statusCode());
    }

    @Test
    @DisplayName("INTERNAL_ERROR: статус 500")
    void internalError_statusCode() {
        assertEquals(500, HttpError.INTERNAL_ERROR.statusCode());
    }

    @Test
    @DisplayName("INTERNAL_ERROR: код 'Internal Server Error'")
    void internalError_code() {
        assertEquals("Internal Server Error", HttpError.INTERNAL_ERROR.code());
    }

    @Test
    @DisplayName("Все константы имеют непустое описание")
    void allConstants_haveNonEmptyDescription() {
        for (HttpError error : HttpError.values()) {
            assertFalse(error.description().isBlank(), error.name() + " должен иметь описание");
        }
    }
}
