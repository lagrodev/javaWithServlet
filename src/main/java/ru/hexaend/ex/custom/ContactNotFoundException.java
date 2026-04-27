package ru.hexaend.ex.custom;

import ru.hexaend.ex.ApplicationException;
import ru.hexaend.ex.HttpError;

/**
 * Исключение, выбрасываемое при попытке обращения к несуществующему контакту.
 * Возвращает HTTP 404 Not Found.
 *
 * @author Vasily Melnik
 */
public class ContactNotFoundException extends ApplicationException {

    /**
     * @param message описание ошибки (например, {@code "Contact with id ... not found"})
     */
    public ContactNotFoundException(String message) {
        super(message, HttpError.NOT_FOUND);
    }
}
