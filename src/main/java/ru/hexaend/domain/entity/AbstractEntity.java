package ru.hexaend.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Базовая сущность с идентификатором и метками времени создания/обновления.
 *
 * <p>Реализует {@code equals}/{@code hashCode} на основе идентификатора.
 * Все доменные объекты приложения наследуются от этого класса.</p>
 *
 * @param <ID> тип идентификатора (например, {@link java.util.UUID})
 * @author Vasily Melnik
 */
public class AbstractEntity<ID> {

    private final ID id;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Создаёт сущность с указанным идентификатором.
     * Метки времени инициализируются текущим моментом.
     *
     * @param id уникальный идентификатор, не {@code null}
     */
    protected AbstractEntity(ID id) {
        this(id, LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Создаёт сущность с явно заданными метками времени.
     * Используется при восстановлении объекта из БД.
     *
     * @param id        уникальный идентификатор, не {@code null}
     * @param createdAt дата создания записи, не {@code null}
     * @param updatedAt дата последнего обновления, не {@code null}
     */
    protected AbstractEntity(ID id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    /**
     * @return уникальный идентификатор сущности
     */
    public ID getId() {
        return this.id;
    }

    /**
     * @return дата и время создания сущности
     */
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * @return дата и время последнего обновления
     */
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * Обновляет метку {@code updatedAt} текущим моментом.
     */
    public void markAsUpdated() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractEntity<?> other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
