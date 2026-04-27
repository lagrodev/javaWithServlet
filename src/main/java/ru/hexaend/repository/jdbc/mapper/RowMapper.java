package ru.hexaend.repository.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Функциональный интерфейс для преобразования текущей строки {@link ResultSet}
 * в объект указанного типа.
 *
 * @param <T> тип результирующего объекта
 * @author Vasily Melnik
 */
@FunctionalInterface
public interface RowMapper<T> {

    /**
     * Преобразует текущую строку результата запроса в объект.
     * Курсор {@code rs} уже установлен на нужную строку.
     *
     * @param rs результат SQL-запроса
     * @return объект, собранный из данных строки
     * @throws SQLException при ошибке чтения данных
     */
    T mapRow(ResultSet rs) throws SQLException;
}
