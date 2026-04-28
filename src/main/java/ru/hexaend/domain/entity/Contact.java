package ru.hexaend.domain.entity;

import ru.hexaend.util.ContactConstraints;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static ru.hexaend.util.ContactConstraints.MAX_PHONES;

/**
 * Контакт телефонного справочника.
 *
 * <p>Хранит имя, фамилию и до {@value ContactConstraints#MAX_PHONES} телефонных номеров. Валидация
 * полей выполняется при создании и при каждом изменении через сеттеры.
 *
 * @author Vasily Melnik
 */
public class Contact extends AbstractEntity<UUID> {

  private final List<String> phoneNumbers;
  private String firstName;
  private String lastName;

  /**
   * Создаёт новый контакт с автоматически сгенерированным UUID.
   *
   * @param firstName    имя контакта, не пустое
   * @param lastName     фамилия контакта, не пустая
   * @param phoneNumbers список телефонных номеров (от 1 до {@value ContactConstraints#MAX_PHONES})
   */
  public Contact(String firstName, String lastName, List<String> phoneNumbers) {
    this(UUID.randomUUID(), firstName, lastName, phoneNumbers);
  }

  /**
   * Создаёт контакт с заданным идентификатором.
   *
   * @param id           UUID контакта
   * @param firstName    имя контакта, не пустое
   * @param lastName     фамилия контакта, не пустая
   * @param phoneNumbers список телефонных номеров (от 1 до {@value ContactConstraints#MAX_PHONES})
   */
  public Contact(UUID id, String firstName, String lastName, List<String> phoneNumbers) {
    super(id);
    validate(firstName, lastName, phoneNumbers);
    this.lastName = lastName.trim();
    this.firstName = firstName.trim();
    // Защитная копия — исключаем внешнюю мутацию переданного списка
    this.phoneNumbers = new ArrayList<>(phoneNumbers);
  }

  /**
   * Создаёт контакт с явно заданными метками времени. Используется при восстановлении объекта из
   * базы данных.
   *
   * @param id           UUID контакта
   * @param firstName    имя контакта
   * @param lastName     фамилия контакта
   * @param phoneNumbers список телефонных номеров
   * @param createdAt    дата создания записи в БД
   * @param updatedAt    дата последнего обновления записи в БД
   */
  public Contact(
          UUID id,
          String firstName,
          String lastName,
          List<String> phoneNumbers,
          LocalDateTime createdAt,
          LocalDateTime updatedAt) {
    super(id, createdAt, updatedAt);
    validate(firstName, lastName, phoneNumbers);
    this.lastName = lastName.trim();
    this.firstName = firstName.trim();
    this.phoneNumbers = new ArrayList<>(phoneNumbers);
  }

  /**
   * Проверяет инварианты контакта: имя/фамилия не пусты, количество номеров от 1 до {@link
   * ContactConstraints#MAX_PHONES}.
   */
  private static void validate(String firstName, String lastName, List<String> phoneNumbers) {
    if (firstName == null || firstName.isBlank()) {
      throw new IllegalArgumentException("Имя не может быть пустым");
    }
    if (lastName == null || lastName.isBlank()) {
      throw new IllegalArgumentException("Фамилия не может быть пустой");
    }
    if (phoneNumbers == null || phoneNumbers.isEmpty()) {
      throw new IllegalArgumentException("Должен быть хотя бы один номер телефона");
    }
    if (phoneNumbers.size() > MAX_PHONES) {
      throw new IllegalArgumentException("Превышен лимит номеров: " + MAX_PHONES);
    }
  }

  /**
   * @return имя контакта
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Устанавливает новое имя контакта. Обновляет метку {@code updatedAt}.
   *
   * @param firstName новое имя, не пустое
   */
  public void setFirstName(String firstName) {
    if (firstName == null || firstName.isBlank()) {
      throw new IllegalArgumentException("Имя не может быть пустым");
    }
    this.markAsUpdated();
    this.firstName = firstName.trim();
  }

  /**
   * @return фамилия контакта
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Устанавливает новую фамилию контакта. Обновляет метку {@code updatedAt}.
   *
   * @param lastName новая фамилия, не пустая
   */
  public void setLastName(String lastName) {
    if (lastName == null || lastName.isBlank()) {
      throw new IllegalArgumentException("Фамилия не может быть пустой");
    }
    this.markAsUpdated();
    this.lastName = lastName.trim();
  }

  /**
   * Возвращает неизменяемый список телефонных номеров контакта.
   *
   * @return неизменяемый список номеров
   */
  public List<String> getPhoneNumbers() {
    return Collections.unmodifiableList(phoneNumbers);
  }

  /**
   * Заменяет все телефонные номера контакта. Обновляет метку {@code updatedAt}.
   *
   * @param phoneNumbers новый список номеров (от 1 до {@value ContactConstraints#MAX_PHONES})
   */
  public void setPhoneNumbers(List<String> phoneNumbers) {
    if (phoneNumbers == null || phoneNumbers.isEmpty()) {
      throw new IllegalArgumentException("Должен быть хотя бы один номер телефона");
    }
    if (phoneNumbers.size() > MAX_PHONES) {
      throw new IllegalArgumentException("Максимальное количество номеров телефона: " + MAX_PHONES);
    }
    this.phoneNumbers.clear();
    this.markAsUpdated();
    this.phoneNumbers.addAll(phoneNumbers);
  }

  /**
   * Добавляет телефонный номер к контакту. Обновляет метку {@code updatedAt}.
   *
   * @param phoneNumber номер телефона, не пустой
   * @throws IllegalStateException    если достигнут лимит номеров
   * @throws IllegalArgumentException если номер пустой или {@code null}
   */
  public void addPhoneNumber(String phoneNumber) {
    if (phoneNumbers.size() >= MAX_PHONES) {
      throw new IllegalStateException("Нельзя добавить больше " + MAX_PHONES + " номеров телефона");
    }
    if (phoneNumber == null || phoneNumber.isBlank()) {
      throw new IllegalArgumentException("Номер телефона не может быть пустым");
    }
    this.markAsUpdated();
    this.phoneNumbers.add(phoneNumber.trim());
  }

  /**
   * @return полное имя в формате «Имя Фамилия»
   */
  public String getFullName() {
    return firstName + " " + lastName;
  }

  @Override
  public String toString() {
    return String.format("%-20s %-15s %s", lastName, firstName, String.join(", ", phoneNumbers));
  }
}
