package ru.hexaend.repository;

import ru.hexaend.entity.AbstractEntity;
import ru.hexaend.entity.Contact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MyRepository<E extends AbstractEntity<ID>, ID> {
    void save(E entity);

    void delete(E entity);

    void deleteById(ID id);

    Optional<E> findById(ID id);

    List<E> findAll();
}
