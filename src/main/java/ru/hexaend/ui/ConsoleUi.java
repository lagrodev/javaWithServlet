package ru.hexaend.ui;

import ru.hexaend.entity.Contact;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.service.impl.PhoneBookServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleUi
{
    private static final String SEPARATOR = "-".repeat(65);
    private final Scanner scanner = new Scanner(System.in);
    private final PhoneBookService service;

    public ConsoleUi(PhoneBookService service)
    {
        this.service = service;
    }

    public void start()
    {
        System.out.println("=".repeat(65));
        System.out.println("           ТЕЛЕФОННЫЙ СПРАВОЧНИК");
        System.out.println("=".repeat(65));

        boolean running = true;
        while (running)
        {
            printMenu();
            String choice = readLine("Выберите пункт меню: ");
            System.out.println();
            switch (choice)
            {
                case "1" -> handleShowAll();
                case "2" -> handleAdd();
                case "3" -> handleEdit();
                case "4" -> handleDelete();
                case "5" -> handleSearchByLastName();
                case "6" -> handleSearchByPhone();
                case "7" -> handleAddPhone();
                case "0" -> running = false;
                default -> System.out.println("Неверный пункт меню. Попробуйте снова.\n");
            }
        }
        System.out.println("До свидания!");
        scanner.close();
    }


    private void printMenu()
    {
        System.out.println(SEPARATOR);
        System.out.println("  1. Показать все контакты");
        System.out.println("  2. Добавить контакт");
        System.out.println("  3. Редактировать контакт");
        System.out.println("  4. Удалить контакт");
        System.out.println("  5. Поиск по фамилии");
        System.out.println("  6. Поиск по номеру телефона");
        System.out.println("  7. Добавить номер к существующему контакту");
        System.out.println("  0. Выход");
        System.out.println(SEPARATOR);
    }


    private void handleShowAll()
    {
        List<Contact> contacts = service.getAllContacts();
        printContacts(contacts, "Справочник пуст.");
    }

    private void handleAdd()
    {
        System.out.println("  [ Добавление нового контакта ]");
        String lastName = readNonBlank("  Фамилия: ");
        String firstName = readNonBlank("  Имя: ");
        List<String> phones = readPhones();

        try
        {
            Contact c = service.addContact(firstName, lastName, phones);
            System.out.println("\n  ✓ Контакт добавлен: " + c.getFullName());
        } catch (IllegalArgumentException e)
        {
            System.out.println("\n  ✗ Ошибка: " + e.getMessage());
        }
        System.out.println();
    }

    private void handleEdit()
    {
        List<Contact> all = service.getAllContacts();
        if (all.isEmpty())
        {
            System.out.println("  Справочник пуст.\n");
            return;
        }

        printContacts(all, "");
        UUID id = selectContactId(all, "  Введите номер контакта для редактирования: ");
        if (id == null)
        {
            return;
        }

        Contact existing = service.getContactById(id);

        System.out.println("  Редактирование: " + existing.getFullName()
                + " | " + String.join(", ", existing.getPhoneNumbers()));
        System.out.println("  (Оставьте поле пустым, чтобы сохранить текущее значение)");

        String lastName = readWithDefault("  Новая фамилия [" + existing.getLastName() + "]: ",
                existing.getLastName());
        String firstName = readWithDefault("  Новое имя [" + existing.getFirstName() + "]: ",
                existing.getFirstName());
        System.out.println("  Введите новые номера (Enter — сохранить текущие):");
        String firstPhoneInput = readLine("  Телефон 1: ");
        List<String> phones;
        if (firstPhoneInput.isBlank())
        {
            phones = existing.getPhoneNumbers();
        }
        else
        {
            phones = new ArrayList<>();
            phones.add(firstPhoneInput.trim());
            for (int i = 2; i <= Contact.MAX_PHONES; i++)
            {
                String p = readLine("  Телефон " + i + " (Enter — пропустить): ");
                if (p.isBlank())
                {
                    break;
                }
                phones.add(p.trim());
            }
        }

        try
        {
            Contact updated = service.editContact(id, firstName, lastName, phones);
            System.out.println("\n  ✓ Контакт обновлён: " + updated.getFullName());
        } catch (IllegalArgumentException e)
        {
            System.out.println("\n  ✗ Ошибка: " + e.getMessage());
        }
        System.out.println();
    }

    private void handleDelete()
    {
        List<Contact> all = service.getAllContacts();
        if (all.isEmpty())
        {
            System.out.println("  Справочник пуст.\n");
            return;
        }

        printContacts(all, "");
        UUID id = selectContactId(all, "  Введите номер контакта для удаления: ");
        if (id == null)
        {
            return;
        }

        Contact contact = service.getContactById(id);


        String confirm = readLine("  Удалить \"" + contact.getFullName() + "\"? (да/нет): ");
        if (confirm.equalsIgnoreCase("да") || confirm.equalsIgnoreCase("д"))
        {
            try
            {
                service.deleteContact(id);
                System.out.println("  ✓ Контакт удалён.\n");
            } catch (IllegalArgumentException e)
            {
                System.out.println("  ✗ Ошибка: " + e.getMessage() + "\n");
            }
        }
        else
        {
            System.out.println("  Удаление отменено.\n");
        }

    }

    private void handleSearchByLastName()
    {
        String query = readNonBlank("  Введите фамилию для поиска: ");
        try
        {
            List<Contact> results = service.searchByLastName(query);
            printContacts(results, "  Ничего не найдено.");
        } catch (IllegalArgumentException e)
        {
            System.out.println("  ✗ " + e.getMessage() + "\n");
        }
    }

    private void handleSearchByPhone()
    {
        String query = readNonBlank("  Введите номер (или часть номера) для поиска: ");
        try
        {
            List<Contact> results = service.searchByPhoneNumber(query);
            printContacts(results, "  Ничего не найдено.");
        } catch (IllegalArgumentException e)
        {
            System.out.println("  ✗ " + e.getMessage() + "\n");
        }
    }

    private void handleAddPhone()
    {
        List<Contact> all = service.getAllContacts();
        if (all.isEmpty())
        {
            System.out.println("  Справочник пуст.\n");
            return;
        }

        printContacts(all, "");
        UUID id = selectContactId(all, "  Введите номер контакта: ");
        if (id == null)
        {
            return;
        }

        Contact contact = service.getContactById(id);

        System.out.println("  Текущие номера: " + String.join(", ", contact.getPhoneNumbers())
                + "  (" + contact.getPhoneNumbers().size() + "/" + Contact.MAX_PHONES + ")");

        if (contact.getPhoneNumbers().size() >= Contact.MAX_PHONES)
        {
            System.out.println("  ✗ Уже достигнут лимит из " + Contact.MAX_PHONES + " номеров.\n");
            return;
        }

        String newPhone = readNonBlank("  Новый номер телефона: ");
        try
        {
            Contact updated = service.addPhoneNumber(id, newPhone);
            System.out.println("  ✓ Номер добавлен. Телефоны: "
                    + String.join(", ", updated.getPhoneNumbers()));
        } catch (IllegalArgumentException | IllegalStateException e)
        {
            System.out.println("  ✗ Ошибка: " + e.getMessage());
        }
        System.out.println();
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void printContacts(List<Contact> contacts, String emptyMessage)
    {
        if (contacts.isEmpty())
        {
            System.out.println("  " + emptyMessage);
        }
        else
        {
            System.out.printf("  %-4s %-20s %-15s  %s%n", "№", "Фамилия", "Имя", "Телефоны");
            System.out.println("  " + SEPARATOR);
            for (int i = 0; i < contacts.size(); i++)
            {
                Contact c = contacts.get(i);
                System.out.printf("  %-4d %-20s %-15s  %s%n",
                        i + 1,
                        c.getLastName(),
                        c.getFirstName(),
                        String.join(", ", c.getPhoneNumbers()));
            }
        }
        System.out.println();
    }

    /**
     * Показывает пронумерованный список и возвращает ID выбранного контакта.
     * Возвращает null при некорректном вводе.
     */
    private UUID selectContactId(List<Contact> contacts, String prompt)
    {
        String input = readLine(prompt);
        int index;
        try
        {
            index = Integer.parseInt(input.trim()) - 1;
        } catch (NumberFormatException e)
        {
            System.out.println("  ✗ Введите корректный номер.\n");
            return null;
        }
        if (index < 0 || index >= contacts.size())
        {
            System.out.println("  ✗ Номер вне допустимого диапазона.\n");
            return null;
        }
        return contacts.get(index).getId();
    }

    private List<String> readPhones()
    {
        List<String> phones = new ArrayList<>();
        for (int i = 1; i <= Contact.MAX_PHONES; i++)
        {
            String suffix = i == 1 ? "" : " (Enter — пропустить)";
            String phone = i == 1
                    ? readNonBlank("  Телефон 1: ")
                    : readLine("  Телефон " + i + suffix + ": ");
            if (phone.isBlank() && i > 1)
            {
                break;
            }
            phones.add(phone.trim());
        }
        return phones;
    }

    private String readLine(String prompt)
    {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private String readNonBlank(String prompt)
    {
        String value;
        do
        {
            value = readLine(prompt);
            if (value.isBlank())
            {
                System.out.println("  ⚠  Поле не может быть пустым.");
            }
        } while (value.isBlank());
        return value.trim();
    }

    private String readWithDefault(String prompt, String defaultValue)
    {
        String value = readLine(prompt);
        return value.isBlank() ? defaultValue : value.trim();
    }
}
