package ru.hexaend.repository;

import ru.hexaend.entity.Contact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends MyRepository<Contact, UUID> {

    List<Contact> findByLastName(String lastName);

    List<Contact> findByPhoneNumber(String phoneNumber);

    List<Contact> findByFirstNameContainingOrLastNameContaining(String query);
}
