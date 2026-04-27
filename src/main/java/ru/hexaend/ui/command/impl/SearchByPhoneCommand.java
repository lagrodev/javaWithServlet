package ru.hexaend.ui.command.impl;

import ru.hexaend.service.PhoneBookService;
import ru.hexaend.ui.command.Command;
import ru.hexaend.ui.command.ConsoleHelper;

/**
 * Команда поиска контактов по номеру телефона (подстрока).
 *
 * @author Vasily Melnik
 */
public class SearchByPhoneCommand implements Command {

    private final PhoneBookService service;
    private final ConsoleHelper helper;

    public SearchByPhoneCommand(PhoneBookService service, ConsoleHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @Override
    public void execute() {
        final String query = helper.readNonBlank("  Введите номер (или часть номера) для поиска: ");
        try {
            helper.printContacts(service.searchByPhoneNumber(query), "Ничего не найдено.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + "\n");
        }
    }
}
