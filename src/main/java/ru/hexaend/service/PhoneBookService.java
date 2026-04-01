package ru.hexaend.service;

import ru.hexaend.entity.Contact;

import java.util.List;
import java.util.UUID;

public interface PhoneBookService {
    Contact addContact(String firstName, String lastName, List<String> phoneNumbers);

    void deleteContact(UUID contactId);

    Contact editContact(UUID contactId, String firstName, String lastName, List<String> phoneNumbers);

    Contact addPhoneNumber(UUID contactId, String phoneNumber);

    List<Contact> getAllContacts();

    List<Contact> searchByPhoneNumber(String phoneNumber);

    List<Contact> searchByLastName(String phoneNumber);


    List<Contact> searchByFirstOrLastName(String query);

    Contact getContactById(UUID contactId);
}
