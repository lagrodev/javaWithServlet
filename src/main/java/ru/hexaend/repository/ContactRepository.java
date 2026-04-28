package ru.hexaend.repository;

import ru.hexaend.domain.entity.Contact;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с контактами телефонного справочника.
 *
 * <p>Расширяет базовый {@link MyRepository} специализированными методами поиска по фамилии, номеру
 * телефона и подстроке имени.
 *
 * @author Vasily Melnik
 */
public interface ContactRepository extends MyRepository<Contact, UUID> {

    /**
     * Ищет контакты по фамилии (регистронезависимо, по подстроке).
     *
     * @param lastName фамилия или её часть
     * @return список найденных контактов
     */
    List<Contact> findByLastName(String lastName);

    /**
     * Ищет контакты, у которых один из номеров содержит указанную подстроку.
     *
     * @param phoneNumber номер телефона или его часть
     * @return список найденных контактов
     */
    List<Contact> findByPhoneNumber(String phoneNumber);

    /**
     * Ищет контакты, у которых имя или фамилия содержат указанную подстроку (регистронезависимо).
     *
     * @param query строка поиска
     * @return список найденных контактов
     */
    List<Contact> findByFirstNameContainingOrLastNameContaining(String query);
}
