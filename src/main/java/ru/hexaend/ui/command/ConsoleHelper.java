package ru.hexaend.ui.command;

import ru.hexaend.domain.entity.Contact;
import ru.hexaend.util.ContactConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static ru.hexaend.util.ContactConstraints.MAX_PHONES;

/**
 * Вспомогательный класс для консольного ввода-вывода.
 *
 * <p>Предоставляет методы для печати таблицы контактов, чтения пользовательского ввода и выбора
 * контакта из списка.
 *
 * @author Vasily Melnik
 */
public class ConsoleHelper {

    /**
     * Ширина таблицы в символах.
     */
    public static final int WIDTH = 65;

    public static final String SEPARATOR = "-".repeat(WIDTH);
    public static final String HEADER = "=".repeat(WIDTH);

    private final Scanner scanner;

    /**
     * @param scanner источник пользовательского ввода
     */
    public ConsoleHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Печатает таблицу контактов или сообщение, если список пуст.
     *
     * @param contacts     список контактов для отображения
     * @param emptyMessage сообщение при пустом списке
     */
    public void printContacts(List<Contact> contacts, String emptyMessage) {
        if (contacts.isEmpty()) {
            System.out.println("  " + emptyMessage);
        } else {
            System.out.printf("  %-4s %-20s %-15s  %s%n", "№", "Фамилия", "Имя", "Телефоны");
            System.out.println("  " + SEPARATOR);
            for (int i = 0; i < contacts.size(); i++) {
                final Contact c = contacts.get(i);
                System.out.printf(
                        "  %-4d %-20s %-15s  %s%n",
                        i + 1, c.getLastName(), c.getFirstName(), String.join(", ", c.getPhoneNumbers()));
            }
        }
        System.out.println();
    }

    /**
     * Просит пользователя выбрать контакт по номеру в списке.
     *
     * @param contacts список контактов
     * @param prompt   приглашение к вводу
     * @return UUID выбранного контакта или {@code null} при некорректном вводе
     */
    public UUID selectContactId(List<Contact> contacts, String prompt) {
        final String input = readLine(prompt);
        final int index;
        try {
            index = Integer.parseInt(input.trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Введите корректный номер.\n");
            return null;
        }
        if (index < 0 || index >= contacts.size()) {
            System.out.println("Номер вне допустимого диапазона.\n");
            return null;
        }
        return contacts.get(index).getId();
    }

    /**
     * Читает до {@value ContactConstraints#MAX_PHONES} телефонных номеров. Первый номер обязателен,
     * остальные можно пропустить (Enter).
     *
     * @return список введённых номеров
     */
    public List<String> readPhones() {
        final List<String> phones = new ArrayList<>();
        for (int i = 1; i <= MAX_PHONES; i++) {
            final String phone =
                    i == 1
                            ? readNonBlank("  Телефон 1: ")
                            : readLine("  Телефон " + i + " (Enter — пропустить): ");
            if (phone.isBlank() && i > 1) break;
            phones.add(phone.trim());
        }
        return phones;
    }

    /**
     * Читает строку из консоли.
     *
     * @param prompt приглашение к вводу
     * @return введённая строка
     */
    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Читает непустую строку, повторяя запрос при пустом вводе.
     *
     * @param prompt приглашение к вводу
     * @return непустая строка (trimmed)
     */
    public String readNonBlank(String prompt) {
        String value;
        do {
            value = readLine(prompt);
            if (value.isBlank()) System.out.println("Поле не может быть пустым.");
        } while (value.isBlank());
        return value.trim();
    }

    /**
     * Читает строку; если пользователь нажал Enter — возвращает значение по умолчанию.
     *
     * @param prompt       приглашение к вводу
     * @param defaultValue значение по умолчанию
     * @return введённое значение или {@code defaultValue}
     */
    public String readWithDefault(String prompt, String defaultValue) {
        final String value = readLine(prompt);
        return value.isBlank() ? defaultValue : value.trim();
    }
}
