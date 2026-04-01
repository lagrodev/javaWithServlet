package ru.hexaend.ui.command.impl;

import ru.hexaend.entity.Contact;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.ui.command.Command;
import ru.hexaend.ui.command.ConsoleHelper;

import java.util.List;
import java.util.UUID;

public class DeleteContactCommand implements Command
{

    private final PhoneBookService service;
    private final ConsoleHelper helper;

    public DeleteContactCommand(PhoneBookService service, ConsoleHelper helper) {
        this.service = service;
        this.helper  = helper;
    }

    @Override
    public void execute() {
        List<Contact> all = service.getAllContacts();
        if (all.isEmpty()) { System.out.println("  Справочник пуст.\n"); return; }

        helper.printContacts(all, "");
        UUID id = helper.selectContactId(all, "  Введите номер контакта для удаления: ");
        if (id == null) return;

        Contact contact = service.getContactById(id);
        String confirm  = helper.readLine("  Удалить \"" + contact.getFullName() + "\"? (да/нет): ");

        if (confirm.equalsIgnoreCase("да") || confirm.equalsIgnoreCase("д")) {
            try {
                service.deleteContact(id);
                System.out.println("  ✓ Контакт удалён.\n");
            } catch (IllegalArgumentException e) {
                System.out.println("  ✗ Ошибка: " + e.getMessage() + "\n");
            }
        } else {
            System.out.println("  Удаление отменено.\n");
        }
    }
}
