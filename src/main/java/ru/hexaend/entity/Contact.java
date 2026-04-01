package ru.hexaend.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Contact {
    private final UUID id;
    private String firstName;
    private String lastName;
    private final List<String> phoneNumbers;

    private static final int MAX_PHONES = 3;

    public static int getMaxPhones() {
        return MAX_PHONES;
    }

    public Contact(String firstName, String lastName, List<String> phoneNumbers) {
        this(UUID.randomUUID(), firstName, lastName, phoneNumbers);
    }

    public Contact(UUID id, String firstName, String lastName, List<String> phoneNumbers) {
        validate(
                firstName, lastName, phoneNumbers
        );
        this.id = id;
        this.lastName = lastName.trim();
        this.firstName = firstName.trim();
        this.phoneNumbers = new ArrayList<>(phoneNumbers); // это чтобы мы не ссылку переприсвоили, а новый лист кинули в сущность

    }

    private static void validate(String firstName, String lastName, List<String> phoneNumbers) {
        if (firstName == null || firstName.isBlank())
        {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (lastName == null || lastName.isBlank())
        {
            throw new IllegalArgumentException("Фамилия не может быть пустой");
        }
        if (phoneNumbers == null || phoneNumbers.isEmpty())
        {
            throw new IllegalArgumentException("Должен быть хотя бы один номер телефона");
        }
        if (phoneNumbers.size() > MAX_PHONES)
        {
            throw new IllegalArgumentException("Превышен лимит номеров: " + MAX_PHONES);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.isBlank())
        {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }

        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.isBlank())
        {
            throw new IllegalArgumentException("Фамилия не может быть пустой");
        }
        this.lastName = lastName.trim();
    }

    public UUID getId() {
        return id;
    }


    public List<String> getPhoneNumbers() {
        return Collections.unmodifiableList(phoneNumbers);
    }

    public void addPhoneNumber(String phoneNumber) // todo тут можно придумать, чтобы проверка была, по правильности ввода номера
    {
        if (phoneNumbers.size() >= MAX_PHONES)
        {
            throw new IllegalStateException("Нельзя добавить больше " + MAX_PHONES + " номеров телефона");
        }
        if (phoneNumber == null || phoneNumber.isBlank())
        {
            throw new IllegalArgumentException("Номер телефона не может быть пустым");
        }
        this.phoneNumbers.add(phoneNumber.trim());
    }


    public void setPhoneNumbers(List<String> phoneNumbers) {
        if (phoneNumbers == null || phoneNumbers.isEmpty())
        {
            throw new IllegalArgumentException("Должен быть хотя бы один номер телефона");
        }
        if (phoneNumbers.size() > MAX_PHONES)
        {
            throw new IllegalArgumentException("Максимальное количество номеров телефона: " + MAX_PHONES);
        }
        this.phoneNumbers.clear();
        this.phoneNumbers.addAll(phoneNumbers);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return String.format(
                "%-20s %-15s %s",
                lastName, firstName, String.join(", ", phoneNumbers)
        );
    }
}
