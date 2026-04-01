package ru.hexaend.repository;

import ru.hexaend.entity.Contact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// тут можно еще забавно прописать, что-то по типу, чтобы ентити была наследником определенного класса
// типа <E extends class> ну и какой-то абстрактный класс создать, там, хз, общие вещт прописать:
// Hash и equals + id и created и updated, и через прокся, чет такое
public interface MyRepository<E, ID> {
    void save(E entity);

    void delete(E entity);

    void deleteById(ID id);

    Optional<Contact> findById(ID id);

    List<Contact> findAll();
}
