package ru.hexaend.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AbstractEntity<ID> {
    public ID id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    protected AbstractEntity(ID id) {
        this(id, LocalDateTime.now(), LocalDateTime.now());
    }

    protected AbstractEntity(ID id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null"); // хм.. а есть ли тут в этом смысл???
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");// мб, просто вешать на них now???
    }

    public ID getId() {
        return this.id;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void markAsUpdated() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractEntity<?> other)) { // лол, java автоматом присваивает other для o 0.0
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
