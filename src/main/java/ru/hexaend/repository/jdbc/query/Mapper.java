package ru.hexaend.repository.jdbc.query;

import ru.hexaend.repository.jdbc.mapper.RowMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Mapper {
    Class<? extends RowMapper<?>> value();
}
