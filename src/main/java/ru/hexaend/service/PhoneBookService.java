package ru.hexaend.service;

import ru.hexaend.domain.entity.Contact;
import ru.hexaend.domain.exeptions.ContactNotFoundException;
import ru.hexaend.domain.exeptions.ValidationException;

import java.util.List;
import java.util.UUID;

/**
 * Сервис бизнес-логики телефонного справочника.
 *
 * <p>Определяет операции создания, редактирования, удаления и поиска
 * контактов. Реализация выполняет валидацию входных данных и делегирует
 * работу с хранилищем в {@link ru.hexaend.repository.ContactRepository}.</p>
 *
 * @author Vasily Melnik
 */
public interface PhoneBookService {

    /**
     * Создаёт новый контакт с указанными данными.
     * Валидирует телефоны через {@link ru.hexaend.util.PhoneValidator}.
     *
     * @param firstName    имя контакта
     * @param lastName     фамилия контакта
     * @param phoneNumbers список телефонных номеров
     * @return созданный контакт
     */
    Contact addContact(String firstName, String lastName, List<String> phoneNumbers);

    /**
     * Удаляет контакт по идентификатору.
     *
     * @param contactId UUID контакта
     * @throws ContactNotFoundException если контакт не найден
     */
    void deleteContact(UUID contactId);

    /**
     * Обновляет данные контакта. Заменяет имя, фамилию и список телефонов.
     *
     * @param contactId    UUID контакта
     * @param firstName    новое имя
     * @param lastName     новая фамилия
     * @param phoneNumbers новый список телефонных номеров
     * @return обновлённый контакт
     * @throws ContactNotFoundException если контакт не найден
     */
    Contact editContact(UUID contactId, String firstName, String lastName, List<String> phoneNumbers);

    /**
     * Возвращает все контакты справочника.
     *
     * @return список контактов, отсортированный по фамилии и имени
     */
    List<Contact> getAllContacts();

    /**
     * Возвращает контакт по идентификатору.
     *
     * @param contactId UUID контакта
     * @return найденный контакт
     * @throws ContactNotFoundException если контакт не найден
     */
    Contact getContactById(UUID contactId);

    /**
     * Поиск контактов по фамилии (подстрока, регистронезависимо).
     *
     * @param lastName фамилия или её часть
     * @return список найденных контактов
     */
    List<Contact> searchByLastName(String lastName);

    /**
     * Поиск контактов по номеру телефона (подстрока).
     *
     * @param phoneNumber номер или его часть
     * @return список найденных контактов
     */
    List<Contact> searchByPhoneNumber(String phoneNumber);

    /**
     * Поиск контактов по имени или фамилии (подстрока, регистронезависимо).
     *
     * @param query строка поиска
     * @return список найденных контактов
     */
    List<Contact> searchByFirstOrLastName(String query);

    /**
     * Добавляет телефонный номер к существующему контакту.
     *
     * @param contactId   UUID контакта
     * @param phoneNumber номер телефона
     * @return обновлённый контакт
     * @throws ContactNotFoundException если контакт не найден
     * @throws ValidationException      если номер невалидный
     */
    Contact addPhoneNumber(UUID contactId, String phoneNumber);
}
