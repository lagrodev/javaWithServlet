package ru.hexaend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hexaend.domain.entity.Contact;
import ru.hexaend.domain.exeptions.ContactNotFoundException;
import ru.hexaend.domain.exeptions.ValidationException;
import ru.hexaend.repository.ContactRepository;
import ru.hexaend.util.PhoneValidator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-тесты для {@link PhoneBookServiceImpl}.
 *
 * @author Vasily Melnik
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PhoneBookServiceImpl")
class PhoneBookServiceImplTest {

    @Mock
    private ContactRepository contactRepository;
    @Mock
    private PhoneValidator phoneValidator;

    private PhoneBookServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PhoneBookServiceImpl(contactRepository, phoneValidator);
    }

    @Nested
    @DisplayName("addContact")
    class AddContact {

        @Test
        @DisplayName("успешно создаёт контакт при валидных данных")
        void validData_createsContact() {
            when(phoneValidator.isValid("+79001234567")).thenReturn(true);

            final Contact result = service.addContact("Иван", "Иванов", List.of("+79001234567"));

            assertNotNull(result);
            assertEquals("Иван", result.getFirstName());
            assertEquals("Иванов", result.getLastName());
            verify(contactRepository).save(any(Contact.class));
        }

        @Test
        @DisplayName("выбрасывает ValidationException при пустом списке телефонов")
        void emptyPhones_throwsValidationException() {
            assertThrows(
                    ValidationException.class, () -> service.addContact("Иван", "Иванов", List.of()));
        }

        @Test
        @DisplayName("выбрасывает ValidationException при null списке телефонов")
        void nullPhones_throwsValidationException() {
            assertThrows(ValidationException.class, () -> service.addContact("Иван", "Иванов", null));
        }

        @Test
        @DisplayName("выбрасывает ValidationException при невалидном номере")
        void invalidPhone_throwsValidationException() {
            when(phoneValidator.isValid("abc")).thenReturn(false);

            assertThrows(
                    ValidationException.class, () -> service.addContact("Иван", "Иванов", List.of("abc")));
        }
    }

    @Nested
    @DisplayName("deleteContact")
    class DeleteContact {

        @Test
        @DisplayName("успешно удаляет существующий контакт")
        void existingContact_deletesSuccessfully() {
            final UUID id = UUID.randomUUID();
            when(contactRepository.findById(id)).thenReturn(Optional.of(mock(Contact.class)));

            service.deleteContact(id);

            verify(contactRepository).deleteById(id);
        }

        @Test
        @DisplayName("выбрасывает ContactNotFoundException для несуществующего контакта")
        void nonExistingContact_throwsContactNotFoundException() {
            final UUID id = UUID.randomUUID();
            when(contactRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ContactNotFoundException.class, () -> service.deleteContact(id));
            verify(contactRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("editContact")
    class EditContact {

        @Test
        @DisplayName("успешно обновляет контакт")
        void existingContact_updatesSuccessfully() {
            final UUID id = UUID.randomUUID();
            final Contact contact = mock(Contact.class);
            when(contactRepository.findById(id)).thenReturn(Optional.of(contact));
            when(phoneValidator.isValid("+7999")).thenReturn(true);

            service.editContact(id, "Пётр", "Петров", List.of("+7999"));

            verify(contact).setFirstName("Пётр");
            verify(contact).setLastName("Петров");
            verify(contact).setPhoneNumbers(List.of("+7999"));
            verify(contactRepository).save(contact);
        }

        @Test
        @DisplayName("выбрасывает ContactNotFoundException для несуществующего контакта")
        void nonExistingContact_throwsContactNotFoundException() {
            final UUID id = UUID.randomUUID();
            when(contactRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(
                    ContactNotFoundException.class,
                    () -> service.editContact(id, "Пётр", "Петров", List.of("+7999")));
        }
    }

    @Nested
    @DisplayName("getContactById")
    class GetContactById {

        @Test
        @DisplayName("возвращает контакт по ID")
        void existingId_returnsContact() {
            final UUID id = UUID.randomUUID();
            final Contact contact = mock(Contact.class);
            when(contactRepository.findById(id)).thenReturn(Optional.of(contact));

            final Contact result = service.getContactById(id);

            assertSame(contact, result);
        }

        @Test
        @DisplayName("выбрасывает ContactNotFoundException для несуществующего ID")
        void nonExistingId_throwsContactNotFoundException() {
            final UUID id = UUID.randomUUID();
            when(contactRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ContactNotFoundException.class, () -> service.getContactById(id));
        }
    }

    @Nested
    @DisplayName("searchByLastName")
    class SearchByLastName {

        @Test
        @DisplayName("пустой запрос возвращает все контакты")
        void blankQuery_returnsAll() {
            when(contactRepository.findAll()).thenReturn(List.of());

            service.searchByLastName("");

            verify(contactRepository).findAll();
            verify(contactRepository, never()).findByLastName(any());
        }

        @Test
        @DisplayName("непустой запрос делегирует в findByLastName")
        void nonBlankQuery_delegatesToFindByLastName() {
            when(contactRepository.findByLastName("Иванов")).thenReturn(List.of());

            service.searchByLastName("Иванов");

            verify(contactRepository).findByLastName("Иванов");
        }
    }

    @Nested
    @DisplayName("addPhoneNumber")
    class AddPhoneNumber {

        @Test
        @DisplayName("выбрасывает ValidationException при невалидном номере")
        void invalidPhone_throwsValidationException() {
            final UUID id = UUID.randomUUID();
            when(contactRepository.findById(id)).thenReturn(Optional.of(mock(Contact.class)));
            when(phoneValidator.isValid("bad")).thenReturn(false);

            assertThrows(ValidationException.class, () -> service.addPhoneNumber(id, "bad"));
        }
    }
}
