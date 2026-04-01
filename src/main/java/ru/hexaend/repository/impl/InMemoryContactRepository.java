package ru.hexaend.repository.impl;

import ru.hexaend.entity.Contact;
import ru.hexaend.repository.ContactRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;


public class InMemoryContactRepository implements ContactRepository {
    private final Map<UUID, Contact> storage = new HashMap<>();


    @Override
    public void save(Contact contact) {
        storage.put(contact.getId(), contact);
    }

    @Override
    public void delete(Contact contact)
    {
        deleteById(contact.getId());
    }

    @Override
    public void deleteById(UUID id)
    {
        storage.remove(id);
    }

    @Override
    public Optional<Contact> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Contact> findAll()
    {
        return storage.values().stream()
                .sorted(ContactComparator.INSTANCE)
                .toList();
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
                .filter(c -> c.getPhoneNumbers().stream()
                        .anyMatch(p -> p.equals(phoneNumber.trim())))
                .sorted(ContactComparator.INSTANCE)
                .toList();
    }

    @Override
    public List<Contact> findByFirstNameContainingOrLastNameContaining(String query) {

        if (query == null || query.isBlank())
        {
            return Collections.emptyList();
        }
        String lowerQuery = query.trim().toLowerCase();
        return storage.values().stream()
                .filter(
                        c -> c.getFirstName().toLowerCase().contains(lowerQuery) ||
                                c.getLastName().toLowerCase().contains(lowerQuery)
                )
                .sorted(ContactComparator.INSTANCE)
                .toList()

                ;
    }

    private static class ContactComparator implements Comparator<Contact> {
        static final ContactComparator INSTANCE = new ContactComparator();

        @Override
        public int compare(Contact c1, Contact c2) {
            int lastNameComparison = c1.getLastName().compareToIgnoreCase(c2.getLastName());
            if (lastNameComparison != 0)
            {
                return lastNameComparison;
            }
            int firstNameComparison = c1.getFirstName().compareToIgnoreCase(c2.getFirstName());
            if (firstNameComparison != 0)
            {
                return firstNameComparison;
            }
            String c1Phone = c1.getPhoneNumbers().isEmpty() ? "" : c1.getPhoneNumbers().getFirst();
            String c2Phone = c2.getPhoneNumbers().isEmpty() ? "" : c2.getPhoneNumbers().getFirst();
            return c1Phone.compareTo(c2Phone);
        }
    }

}
