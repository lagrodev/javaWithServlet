package ru.hexaend.ui.command.impl;

import ru.hexaend.domain.entity.Contact;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.ui.command.Command;
import ru.hexaend.ui.command.ConsoleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ru.hexaend.util.ContactConstraints.MAX_PHONES;

/**
 * Команда редактирования существующего контакта.
 * Позволяет изменить фамилию, имя и список телефонных номеров.
 *
 * @author Vasily Melnik
 */
public class EditContactCommand implements Command {

    private final PhoneBookService service;
    private final ConsoleHelper helper;

    public EditContactCommand(PhoneBookService service, ConsoleHelper helper) {
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
        final UUID id = helper.selectContactId(all, "  Введите номер контакта для редактирования: ");
        if (id == null) return;

        final Contact existing = service.getContactById(id);
        System.out.println("  Редактирование: " + existing.getFullName()
                + " | " + String.join(", ", existing.getPhoneNumbers()));
        System.out.println("  (Оставьте поле пустым, чтобы сохранить текущее значение)");

        final String lastName = helper.readWithDefault("  Новая фамилия [" + existing.getLastName() + "]: ", existing.getLastName());
        final String firstName = helper.readWithDefault("  Новое имя [" + existing.getFirstName() + "]: ", existing.getFirstName());
        final List<String> phones = readUpdatedPhones(existing);

        try {
            final Contact updated = service.editContact(id, firstName, lastName, phones);
            System.out.println("\nКонтакт обновлён: " + updated.getFullName());
        } catch (IllegalArgumentException e) {
            System.out.println("\nОшибка: " + e.getMessage());
        }
        System.out.println();
    }

    private List<String> readUpdatedPhones(Contact existing) {
        System.out.println("Введите новые номера (Enter — сохранить текущие):");
        final String first = helper.readLine("  Телефон 1: ");
        if (first.isBlank()) return existing.getPhoneNumbers();

        final List<String> phones = new ArrayList<>();
        phones.add(first.trim());
        for (int i = 2; i <= MAX_PHONES; i++) {
            final String p = helper.readLine("  Телефон " + i + " (Enter — пропустить): ");
            if (p.isBlank()) break;
            phones.add(p.trim());
        }
        return phones;
    }
}
