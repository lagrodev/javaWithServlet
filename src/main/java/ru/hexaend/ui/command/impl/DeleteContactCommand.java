package ru.hexaend.ui.command.impl;

import ru.hexaend.domain.entity.Contact;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.ui.command.Command;
import ru.hexaend.ui.command.ConsoleHelper;

import java.util.List;
import java.util.UUID;

/**
 * Команда удаления контакта из справочника. Запрашивает подтверждение перед удалением.
 *
 * @author Vasily Melnik
 */
public class DeleteContactCommand implements Command {

    private final PhoneBookService service;
    private final ConsoleHelper helper;

    public DeleteContactCommand(PhoneBookService service, ConsoleHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @Override
    public void execute() {
        final List<Contact> all = service.getAllContacts();
        if (all.isEmpty()) {
            System.out.println("  Справочник пуст.\n");
            return;
        }

        helper.printContacts(all, "");
        final UUID id = helper.selectContactId(all, "  Введите номер контакта для удаления: ");
        if (id == null) return;

        final Contact contact = service.getContactById(id);
        final String confirm =
                helper.readLine("  Удалить \"" + contact.getFullName() + "\"? (да/нет): ");

        if (confirm.equalsIgnoreCase("да") || confirm.equalsIgnoreCase("д")) {
            try {
                service.deleteContact(id);
                System.out.println("Контакт удалён.\n");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + "\n");
            }
        } else {
            System.out.println("Удаление отменено.\n");
        }
    }
}
