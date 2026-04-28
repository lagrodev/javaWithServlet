package ru.hexaend.repository.impl;

import ru.hexaend.domain.entity.Contact;
import ru.hexaend.repository.ContactRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory реализация {@link ContactRepository} на основе {@link HashMap}.
 *
 * <p>Используется для локальной разработки и тестирования без подключения к БД. Данные хранятся в
 * оперативной памяти и теряются при перезапуске приложения.
 *
 * @author Vasily Melnik
 */
public class InMemoryContactRepository implements ContactRepository {

  private final Map<UUID, Contact> storage = new HashMap<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public void save(Contact contact) {
    storage.put(contact.getId(), contact);
  }

  @Override
  public void delete(Contact contact) {
    deleteById(contact.getId());
  }

  @Override
  public void deleteById(UUID id) {
    storage.remove(id);
  }

  @Override
  public Optional<Contact> findById(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public List<Contact> findAll() {
    return storage.values().stream().sorted(ContactComparator.INSTANCE).toList();
  }

  @Override
  public List<Contact> findByLastName(String lastName) {
    return storage.values().stream()
            .filter(c -> c.getLastName().equalsIgnoreCase(lastName.trim()))
            .sorted(ContactComparator.INSTANCE)
            .toList();
  }

  @Override
  public List<Contact> findByPhoneNumber(String phoneNumber) {
    return storage.values().stream()
            .filter(c -> c.getPhoneNumbers().stream().anyMatch(p -> p.equals(phoneNumber.trim())))
            .sorted(ContactComparator.INSTANCE)
            .toList();
  }

  @Override
  public List<Contact> findByFirstNameContainingOrLastNameContaining(String query) {

    if (query == null || query.isBlank()) {
      return Collections.emptyList();
    }
    final String lowerQuery = query.trim().toLowerCase();
    return storage.values().stream()
            .filter(
                    c ->
                            c.getFirstName().toLowerCase().contains(lowerQuery)
                                    || c.getLastName().toLowerCase().contains(lowerQuery))
            .sorted(ContactComparator.INSTANCE)
            .toList();
  }

  /**
   * Компаратор для сортировки контактов: по фамилии, затем по имени, затем по первому телефону
   * (регистронезависимо).
   */
  private static class ContactComparator implements Comparator<Contact> {
    static final ContactComparator INSTANCE = new ContactComparator();

    @Override
    public int compare(Contact c1, Contact c2) {
      final int lastNameComparison = c1.getLastName().compareToIgnoreCase(c2.getLastName());
      if (lastNameComparison != 0) {
        return lastNameComparison;
      }
      final int firstNameComparison = c1.getFirstName().compareToIgnoreCase(c2.getFirstName());
      if (firstNameComparison != 0) {
        return firstNameComparison;
      }
      final String c1Phone = c1.getPhoneNumbers().isEmpty() ? "" : c1.getPhoneNumbers().getFirst();
      final String c2Phone = c2.getPhoneNumbers().isEmpty() ? "" : c2.getPhoneNumbers().getFirst();
      return c1Phone.compareTo(c2Phone);
    }
  }
}
