package ru.hexaend.ex.custom;

import ru.hexaend.ex.ApplicationException;

public class ContactNotFoundException extends ApplicationException {
    public ContactNotFoundException(String message) {
        super(message, 404);
    }
}
