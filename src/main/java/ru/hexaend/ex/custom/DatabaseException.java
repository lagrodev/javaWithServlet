package ru.hexaend.ex.custom;

import ru.hexaend.ex.ApplicationException;
import ru.hexaend.ex.HttpError;

/**
 * Исключение, выбрасываемое при ошибке взаимодействия с базой данных.
 * Возвращает HTTP 500 Internal Server Error.
 *
 * @author Vasily Melnik
 */
public class DatabaseException extends ApplicationException {

    /**
     * @param message описание ошибки БД
     */
    public DatabaseException(String message) {
        super(message, HttpError.INTERNAL_ERROR);
    }

    /**
     * @param message описание ошибки БД
     * @param cause   исходное исключение (обычно {@link java.sql.SQLException})
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause, HttpError.INTERNAL_ERROR);
    }
}
