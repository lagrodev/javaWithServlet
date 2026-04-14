package ru.hexaend.service.impl;

import ru.hexaend.entity.Contact;
import ru.hexaend.repository.ContactRepository;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.util.PhoneValidator;

import java.util.List;
import java.util.UUID;

public class PhoneBookServiceImpl implements PhoneBookService {

    private final ContactRepository contactRepository;
    private final PhoneValidator phoneValidator;

    public PhoneBookServiceImpl(ContactRepository contactRepository, PhoneValidator phoneValidator) {
        this.contactRepository = contactRepository;
        this.phoneValidator = phoneValidator;
    }

    @Override
    public Contact addContact(String firstName, String lastName, List<String> phoneNumbers) {
        validatePhoneNumber(phoneNumbers);
        Contact contact = new Contact(
                firstName, lastName, phoneNumbers
        );
        contactRepository.save(contact);
        return contact;
    }

    @Override
    public void deleteContact(UUID contactId) {
        contactRepository.findById(contactId).orElseThrow(() ->
                new RuntimeException("Contact with id " + contactId + " not found"));
        contactRepository.deleteById(contactId);
    }

    @Override
    public Contact editContact(UUID contactId, String firstName, String lastName, List<String> phoneNumbers) {
        Contact contact = contactRepository.findById(contactId).orElseThrow(
                () -> new RuntimeException("Contact with id " + contactId + " not found")
        );
        validatePhoneNumber(phoneNumbers);
        contact.setLastName(lastName);
        contact.setFirstName(firstName);
        contact.setPhoneNumbers(phoneNumbers);
        contactRepository.save(contact);
        return contact;

    }

    @Override
    public Contact addPhoneNumber(UUID contactId, String phoneNumber) {
        Contact contact = contactRepository.findById(
                contactId
        ).orElseThrow(() -> new RuntimeException("Contact with id " + contactId + " not found"));

        if (!phoneValidator.isValid(phoneNumber))
        {
            throw new IllegalArgumentException(
                    "Phone number " + phoneNumber + " is invalid"
            );
        }
        contact.addPhoneNumber(phoneNumber);
        contactRepository.save(contact);
        return contact;

    }

    @Override
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @Override
    public List<Contact> searchByLastName(String lastName) {
        if (lastName == null || lastName.isBlank())
        {
            return contactRepository.findAll();
        }
        return contactRepository.findByLastName(lastName);
    }

    @Override
    public List<Contact> searchByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank())
        {
            return contactRepository.findAll();
        }
        return contactRepository.findByPhoneNumber(phoneNumber);
    }


    @Override
    public List<Contact> searchByFirstOrLastName(String query) {
        if (query == null || query.isBlank())
        {
            return contactRepository.findAll();
        }
        return contactRepository.findByFirstNameContainingOrLastNameContaining(query);
    }

    @Override
    public Contact getContactById(UUID contactId) {
        return contactRepository.findById(contactId).orElseThrow(
                () -> new RuntimeException("Contact with id " + contactId + " not found")
        );
    }

    private void validatePhoneNumber(List<String> phoneNumbers) {
        if (phoneNumbers == null || phoneNumbers.isEmpty())
        {
            throw new IllegalArgumentException("Phone number is empty");
        }
        for (String phoneNumber : phoneNumbers)
        {
            if (!phoneValidator.isValid(phoneNumber))
            {
                throw new IllegalArgumentException("Phone number is invalid");
            }
        }
    }
}
