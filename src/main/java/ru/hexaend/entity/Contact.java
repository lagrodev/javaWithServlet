package ru.hexaend.entity;

import ru.hexaend.domain.ContactConstrains;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static ru.hexaend.domain.ContactConstrains.MAX_PHONES;

public class Contact extends AbstractEntity<UUID> {
    // по поводу {} - на каких строках, тут не согласен, я ХЗ как будет у меня на раБоте, но знаю нескольких знакомых,
    // в том числе тип, у кого ноут спер, с которого требуют после класса/метода и т.п. нажимать enter и ток потом {
    // так что, это чисто вкусовщина
    // мб, дудовщина с паскаля, хзхзхз
    private String firstName;
    private String lastName;
    private final List<String> phoneNumbers;

    public Contact(String firstName, String lastName, List<String> phoneNumbers) {
        this(UUID.randomUUID(), firstName, lastName, phoneNumbers);
    }

    public Contact(UUID id, String firstName, String lastName, List<String> phoneNumbers) {
        super(id);
        validate(
                firstName, lastName, phoneNumbers
        );
        this.lastName = lastName.trim();
        this.firstName = firstName.trim();
        this.phoneNumbers = new ArrayList<>(phoneNumbers); // это чтобы мы не ссылку переприсвоили, а новый лист кинули в сущность
    }


    public Contact(
            UUID id, String firstName, String lastName,
            List<String> phoneNumbers,
            LocalDateTime createdAt, LocalDateTime updatedAt
    ){
        super(id, createdAt, updatedAt);
        validate(
                firstName, lastName, phoneNumbers
        );
        this.lastName = lastName.trim();
        this.firstName = firstName.trim();
        this.phoneNumbers = new  ArrayList<>(phoneNumbers);

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
        this.markAsUpdated();
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
        this.markAsUpdated();
        this.lastName = lastName.trim();
    }

    public UUID getId() {
        return id;
    }


    public List<String> getPhoneNumbers() {
        return Collections.unmodifiableList(phoneNumbers);
    }

    public void addPhoneNumber(String phoneNumber) // todo тут можно придумать, чтобы проверка была, по правильности ввода номера, как в чистом ddd
    // но мне впадлу, и это все будет в сервисе :(
    {
        if (phoneNumbers.size() >= MAX_PHONES)
        {
            throw new IllegalStateException("Нельзя добавить больше " + MAX_PHONES + " номеров телефона");
        }
        if (phoneNumber == null || phoneNumber.isBlank())
        {
            throw new IllegalArgumentException("Номер телефона не может быть пустым");
        }
        this.markAsUpdated();
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
        this.markAsUpdated();
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
