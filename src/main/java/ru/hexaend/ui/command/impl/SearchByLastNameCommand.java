package ru.hexaend.ui.command.impl;

import ru.hexaend.service.PhoneBookService;
import ru.hexaend.ui.command.Command;
import ru.hexaend.ui.command.ConsoleHelper;

/**
 * Команда поиска контактов по фамилии (подстрока, регистронезависимо).
 *
 * @author Vasily Melnik
 */
public class SearchByLastNameCommand implements Command {

    private final PhoneBookService service;
    private final ConsoleHelper helper;

    public SearchByLastNameCommand(PhoneBookService service, ConsoleHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @Override
    public void execute() {
        final String query = helper.readNonBlank("  Введите фамилию для поиска: ");
        try {
            helper.printContacts(service.searchByLastName(query), "Ничего не найдено.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + "\n");
        }
    }
}
