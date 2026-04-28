package ru.hexaend.ui.command.impl;

import ru.hexaend.domain.entity.Contact;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.ui.command.Command;
import ru.hexaend.ui.command.ConsoleHelper;
import ru.hexaend.util.ContactConstraints;

import java.util.List;
import java.util.UUID;

import static ru.hexaend.util.ContactConstraints.MAX_PHONES;

/**
 * Команда добавления телефонного номера к существующему контакту. Проверяет лимит {@value
 * ContactConstraints#MAX_PHONES} номеров.
 *
 * @author Vasily Melnik
 */
public class AddPhoneCommand implements Command {

    private final PhoneBookService service;
    private final ConsoleHelper helper;

    public AddPhoneCommand(PhoneBookService service, ConsoleHelper helper) {
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
        final UUID id = helper.selectContactId(all, "  Введите номер контакта: ");
        if (id == null) return;

        final Contact contact = service.getContactById(id);
        System.out.println(
                "  Текущие номера: "
                        + String.join(", ", contact.getPhoneNumbers())
                        + "  ("
                        + contact.getPhoneNumbers().size()
                        + "/"
                        + MAX_PHONES
                        + ")");

        if (contact.getPhoneNumbers().size() >= MAX_PHONES) {
            System.out.println(" Уже достигнут лимит из " + MAX_PHONES + " номеров.\n");
            return;
        }

        try {
            final Contact updated =
                    service.addPhoneNumber(id, helper.readNonBlank("  Новый номер телефона: "));
            System.out.println(
                    "Номер добавлен. Телефоны: " + String.join(", ", updated.getPhoneNumbers()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        System.out.println();
    }
}
