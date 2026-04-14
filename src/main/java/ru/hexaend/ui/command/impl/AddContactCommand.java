package ru.hexaend.ui.command.impl;

import ru.hexaend.entity.Contact;
import ru.hexaend.service.PhoneBookService;import ru.hexaend.ui.command.Command;import ru.hexaend.ui.command.ConsoleHelper;

public class AddContactCommand implements Command {

    private final PhoneBookService service;
    private final ConsoleHelper    helper;

    public AddContactCommand(PhoneBookService service, ConsoleHelper helper) {
        this.service = service;
        this.helper  = helper;
    }

    @Override
    public void execute() {
        System.out.println("  [ Добавление нового контакта ]");
        String lastName  = helper.readNonBlank("  Фамилия: ");
        String firstName = helper.readNonBlank("  Имя: ");

        try {
            Contact c = service.addContact(firstName, lastName, helper.readPhones());
            System.out.println("\n  ✓ Контакт добавлен: " + c.getFullName());
        } catch (IllegalArgumentException e) {
            System.out.println("\n  ✗ Ошибка: " + e.getMessage());
        }
        System.out.println();
    }
}
