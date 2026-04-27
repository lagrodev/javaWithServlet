package ru.hexaend.ui.command.impl;

import ru.hexaend.util.ContactConstraints;
import ru.hexaend.domain.entity.Contact;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.ui.command.Command;
import ru.hexaend.ui.command.ConsoleHelper;

/**
 * Команда добавления нового контакта.
 * Запрашивает фамилию, имя и до {@value ContactConstraints#MAX_PHONES} телефонов.
 *
 * @author Vasily Melnik
 */
public class AddContactCommand implements Command {

    private final PhoneBookService service;
    private final ConsoleHelper helper;

    public AddContactCommand(PhoneBookService service, ConsoleHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @Override
    public void execute() {
        System.out.println("  [ Добавление нового контакта ]");
        final String lastName = helper.readNonBlank("  Фамилия: ");
        final String firstName = helper.readNonBlank("  Имя: ");

        try {
            final Contact c = service.addContact(firstName, lastName, helper.readPhones());
            System.out.println("\n Контакт добавлен: " + c.getFullName());
        } catch (IllegalArgumentException e) {
            System.out.println("\n Ошибка: " + e.getMessage());
        }
        System.out.println();
    }
}
