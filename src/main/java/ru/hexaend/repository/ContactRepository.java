package ru.hexaend.repository;

import ru.hexaend.entity.Contact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactRepository
{
    void save(Contact contact);

    void delete(Contact contact);

    void deleteById(UUID id);

    Optional<Contact> findById(UUID id);

    List<Contact> findAll();

    List<Contact> findByLastName(String lastName);

    List<Contact> findByPhoneNumber(String phoneNumber);

    List<Contact> findByFirstNameContainingOrLastNameContaining(String query);
}
