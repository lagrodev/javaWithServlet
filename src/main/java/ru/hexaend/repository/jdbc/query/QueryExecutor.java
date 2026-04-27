package ru.hexaend.repository.jdbc.query;

import ru.hexaend.domain.exeptions.DatabaseException;
import ru.hexaend.repository.jdbc.DataSourceProvider;
import ru.hexaend.domain.mapper.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Исполнитель SQL-запросов поверх {@link DataSourceProvider}.
 *
 * <p>Инкапсулирует получение соединения, подготовку {@link PreparedStatement},
 * маппинг результатов и корректное освобождение ресурсов.</p>
 *
 * @author Vasily Melnik
 */
public class QueryExecutor {

    private final DataSourceProvider ds;

    /**
     * @param ds провайдер JDBC-соединений
     */
    public QueryExecutor(DataSourceProvider ds) {
        this.ds = Objects.requireNonNull(ds, "DataSourceProvider must not be null");
    }

    /**
     * Выполняет SELECT-запрос и возвращает список объектов.
     *
     * @param sql    SQL-запрос с плейсхолдерами {@code ?}
     * @param mapper маппер строки результата в объект
     * @param params параметры запроса (подставляются вместо {@code ?})
     * @param <T>    тип результирующего объекта
     * @return список объектов (может быть пустым)
     */
    public <T> List<T> queryList(String sql, RowMapper<T> mapper, Object... params) {
        Objects.requireNonNull(sql, "sql is null");
        Objects.requireNonNull(mapper, "mapper is null");
        Objects.requireNonNull(params, "params is null");

        try (final Connection conn = ds.getConnection();
             final PreparedStatement ps = prepare(conn, sql, params);
             final ResultSet rs = ps.executeQuery()) {

            final List<T> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapper.mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка выполнения запроса: " + sql, e);
        }
    }

    /**
     * Выполняет SELECT-запрос, ожидая не более одной строки.
     *
     * @param sql    SQL-запрос с плейсхолдерами {@code ?}
     * @param mapper маппер строки результата в объект
     * @param params параметры запроса
     * @param <T>    тип результирующего объекта
     * @return {@link Optional} с объектом, или пустой
     * @throws DatabaseException если запрос вернул более одной строки
     */
    public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
        final List<T> list = queryList(sql, mapper, params);
        if (list.size() > 1) {
            throw new DatabaseException("Ожидалась одна запись, получено: " + list.size());
        }
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    /**
     * Выполняет INSERT/UPDATE/DELETE запрос.
     *
     * @param sql    SQL-запрос с плейсхолдерами {@code ?}
     * @param params параметры запроса
     * @return количество затронутых строк
     */
    public int update(String sql, Object... params) {
        try (final Connection conn = ds.getConnection();
             final PreparedStatement ps = prepare(conn, sql, params)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка выполнения update: " + sql, e);
        }
    }

    /**
     * Создаёт {@link PreparedStatement} и подставляет параметры.
     */
    private PreparedStatement prepare(Connection conn, String sql, Object[] params) throws SQLException {
        final PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps;
    }
}
