package ru.hexaend.domain.exeptions;

import ru.hexaend.domain.dto.HttpError;

/**
 * Исключение, выбрасываемое при нарушении правил валидации входных данных. Возвращает HTTP 400 Bad
 * Request.
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
