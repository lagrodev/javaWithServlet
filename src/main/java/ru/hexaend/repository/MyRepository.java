package ru.hexaend.repository;

import ru.hexaend.entity.Contact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MyRepository<E, ID> {
    void save(E entity);

    void delete(E entity);

    void deleteById(ID id);

    Optional<Contact> findById(ID id);

    List<Contact> findAll();
}
