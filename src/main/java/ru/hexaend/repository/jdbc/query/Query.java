package ru.hexaend.repository.jdbc.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // аннотация сохраняется после компиляции + хранится в jvm
@Target(ElementType.METHOD) // аннотировать можно только методы, не классы, и
public @interface Query {
    String value();
}
