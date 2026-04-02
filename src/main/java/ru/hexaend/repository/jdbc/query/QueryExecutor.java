package ru.hexaend.repository.jdbc.query;

import ru.hexaend.repository.jdbc.DataSourceProvider;
import ru.hexaend.repository.jdbc.mapper.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class QueryExecutor {
    private final DataSourceProvider ds;

    public QueryExecutor(DataSourceProvider ds) {
        this.ds = ds;
    }

    public <T>List<T> queryList(String sql, RowMapper<T> mapper, Object... params) {
        Objects.requireNonNull(sql, "sql is null");
        Objects.requireNonNull(mapper, "mapper is null");
        Objects.requireNonNull(params, "param is null");
        try (Connection conn = ds.getConnection()) {

            PreparedStatement ps = prepare(conn, sql, params);
            ResultSet rs = ps.executeQuery();

            List<T> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapper.mapRow(rs));
            }
            return list;

        }
        catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения запроса: " + sql, e);
        }
    }

    public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params)  {
        List<T> list = queryList(sql, mapper, params);
        if (list.size() > 1) {
            throw new RuntimeException("Ожидалась одна запись, получено: " + list.size());
        }
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    public int update(String sql, Object... params){
        try (Connection conn = ds.getConnection()) {
            PreparedStatement ps = prepare(conn, sql, params);
            return ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения update: " + sql, e);
        }
    }


    private PreparedStatement prepare(Connection conn, String sql, Object[] param) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < param.length; i++) {
            ps.setObject(i + 1, param[i]);
        }
        return ps;
    }
}
