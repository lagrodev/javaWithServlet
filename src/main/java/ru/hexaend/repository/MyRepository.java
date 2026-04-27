package ru.hexaend.repository;

import ru.hexaend.entity.AbstractEntity;

import java.util.List;
import java.util.Optional;

/**
 * Обобщённый репозиторий для CRUD-операций над сущностями.
 *
 * @param <E>  тип сущности, наследующей {@link AbstractEntity}
 * @param <ID> тип идентификатора сущности
 * @author Vasily Melnik
 */
public interface MyRepository<E extends AbstractEntity<ID>, ID> {

    /**
     * Сохраняет сущность (вставка или обновление).
     *
     * @param entity сущность для сохранения
     */
    void save(E entity);

    /**
     * Удаляет сущность.
     *
     * @param entity сущность для удаления
     */
    void delete(E entity);

    /**
     * Удаляет сущность по идентификатору.
     *
     * @param id идентификатор удаляемой сущности
     */
    void deleteById(ID id);

    /**
     * Ищет сущность по идентификатору.
     *
     * @param id идентификатор
     * @return {@link Optional} с сущностью, или пустой если не найдена
     */
    Optional<E> findById(ID id);

    /**
     * Возвращает все сущности, отсортированные по умолчанию.
     *
     * @return список всех сущностей
     */
    List<E> findAll();
}
