package ru.hexaend.ex.custom;

import ru.hexaend.ex.ApplicationException;

public class ValidationException extends ApplicationException {
    public ValidationException(String message) {
        super(message, 400);
    }
}
