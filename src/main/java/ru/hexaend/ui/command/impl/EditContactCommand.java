package ru.hexaend.ui.command.impl;

import ru.hexaend.entity.Contact;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.ui.command.Command;
import ru.hexaend.ui.command.ConsoleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditContactCommand implements Command
{

    private final PhoneBookService service;
    private final ConsoleHelper helper;

    public EditContactCommand(PhoneBookService service, ConsoleHelper helper) {
        this.service = service;
        this.helper  = helper;
    }

    @Override
    public void execute() {
        List<Contact> all = service.getAllContacts();
        if (all.isEmpty()) { System.out.println("  Справочник пуст.\n"); return; }

        helper.printContacts(all, "");
        UUID id = helper.selectContactId(all, "  Введите номер контакта для редактирования: ");
        if (id == null) return;

        Contact existing = service.getContactById(id);
        System.out.println("  Редактирование: " + existing.getFullName()
                + " | " + String.join(", ", existing.getPhoneNumbers()));
        System.out.println("  (Оставьте поле пустым, чтобы сохранить текущее значение)");

        String lastName  = helper.readWithDefault("  Новая фамилия [" + existing.getLastName()  + "]: ", existing.getLastName());
        String firstName = helper.readWithDefault("  Новое имя ["     + existing.getFirstName() + "]: ", existing.getFirstName());
        List<String> phones = readUpdatedPhones(existing);

        try {
            Contact updated = service.editContact(id, firstName, lastName, phones);
            System.out.println("\n  ✓ Контакт обновлён: " + updated.getFullName());
        } catch (IllegalArgumentException e) {
            System.out.println("\n  ✗ Ошибка: " + e.getMessage());
        }
        System.out.println();
    }

    private List<String> readUpdatedPhones(Contact existing) {
        System.out.println("  Введите новые номера (Enter — сохранить текущие):");
        String first = helper.readLine("  Телефон 1: ");
        if (first.isBlank()) return existing.getPhoneNumbers();

        List<String> phones = new ArrayList<>();
        phones.add(first.trim());
        for (int i = 2; i <= Contact.getMaxPhones(); i++) {
            String p = helper.readLine("  Телефон " + i + " (Enter — пропустить): ");
            if (p.isBlank()) break;
            phones.add(p.trim());
        }
        return phones;
    }
}
