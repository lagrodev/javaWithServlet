package ru.hexaend.ui.command;

import ru.hexaend.entity.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import static ru.hexaend.domain.ContactConstrains.MAX_PHONES;

public class ConsoleHelper {

    public static final int    WIDTH     = 65;
    public static final String SEPARATOR = "-".repeat(WIDTH);
    public static final String HEADER    = "=".repeat(WIDTH);

    private final Scanner scanner;

    public ConsoleHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public void printContacts(List<Contact> contacts, String emptyMessage) {
        if (contacts.isEmpty()) {
            System.out.println("  " + emptyMessage);
        } else {
            System.out.printf("  %-4s %-20s %-15s  %s%n", "№", "Фамилия", "Имя", "Телефоны");
            System.out.println("  " + SEPARATOR);
            for (int i = 0; i < contacts.size(); i++) {
                Contact c = contacts.get(i);
                System.out.printf("  %-4d %-20s %-15s  %s%n",
                        i + 1, c.getLastName(), c.getFirstName(),
                        String.join(", ", c.getPhoneNumbers()));
            }
        }
        System.out.println();
    }

    public UUID selectContactId(List<Contact> contacts, String prompt) {
        String input = readLine(prompt);
        int index;
        try {
            index = Integer.parseInt(input.trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("  ✗ Введите корректный номер.\n");
            return null;
        }
        if (index < 0 || index >= contacts.size()) {
            System.out.println("  ✗ Номер вне допустимого диапазона.\n");
            return null;
        }
        return contacts.get(index).getId();
    }

    public List<String> readPhones() {
        List<String> phones = new ArrayList<>();
        for (int i = 1; i <= MAX_PHONES; i++) {
            String phone = i == 1
                    ? readNonBlank("  Телефон 1: ")
                    : readLine("  Телефон " + i + " (Enter — пропустить): ");
            if (phone.isBlank() && i > 1) break;
            phones.add(phone.trim());
        }
        return phones;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public String readNonBlank(String prompt) {
        String value;
        do {
            value = readLine(prompt);
            if (value.isBlank()) System.out.println("  ⚠  Поле не может быть пустым.");
        } while (value.isBlank());
        return value.trim();
    }

    public String readWithDefault(String prompt, String defaultValue) {
        String value = readLine(prompt);
        return value.isBlank() ? defaultValue : value.trim();
    }
}
