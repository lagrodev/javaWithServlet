package ru.hexaend.repository.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PhoneMapper implements RowMapper<String> {
    @Override
    public String mapRow(ResultSet rs) throws SQLException {
        return rs.getString("phone");
    }
}
