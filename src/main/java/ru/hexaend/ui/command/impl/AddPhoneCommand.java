package ru.hexaend.ui.command.impl;

import ru.hexaend.entity.Contact;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.ui.command.Command;
import ru.hexaend.ui.command.ConsoleHelper;

import java.util.List;
import java.util.UUID;

public class AddPhoneCommand implements Command
{

    private final PhoneBookService service;
    private final ConsoleHelper helper;

    public AddPhoneCommand(PhoneBookService service, ConsoleHelper helper) {
        this.service = service;
        this.helper  = helper;
    }

    @Override
    public void execute() {
        List<Contact> all = service.getAllContacts();
        if (all.isEmpty()) { System.out.println("  Справочник пуст.\n"); return; }

        helper.printContacts(all, "");
        UUID id = helper.selectContactId(all, "  Введите номер контакта: ");
        if (id == null) return;

        Contact contact = service.getContactById(id);
        System.out.println("  Текущие номера: " + String.join(", ", contact.getPhoneNumbers())
                + "  (" + contact.getPhoneNumbers().size() + "/" + Contact.getMaxPhones() + ")");

        if (contact.getPhoneNumbers().size() >= Contact.getMaxPhones()) {
            System.out.println("  ✗ Уже достигнут лимит из " + Contact.getMaxPhones() + " номеров.\n");
            return;
        }

        try {
            Contact updated = service.addPhoneNumber(id, helper.readNonBlank("  Новый номер телефона: "));
            System.out.println("  ✓ Номер добавлен. Телефоны: " + String.join(", ", updated.getPhoneNumbers()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("  ✗ Ошибка: " + e.getMessage());
        }
        System.out.println();
    }
}
