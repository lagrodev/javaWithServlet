package ru.hexaend.ex.custom;

import ru.hexaend.ex.ApplicationException;
import ru.hexaend.ex.HttpError;

/**
 * Исключение, выбрасываемое при нарушении правил валидации входных данных.
 * Возвращает HTTP 400 Bad Request.
 *
 * @author Vasily Melnik
 */
public class ValidationException extends ApplicationException {

    /**
     * @param message описание нарушения валидации
     */
    public ValidationException(String message) {
        super(message, HttpError.BAD_REQUEST);
    }
}
