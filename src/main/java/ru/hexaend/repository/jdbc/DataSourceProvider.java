package ru.hexaend.repository.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSourceProvider
{
    Connection getConnection() throws SQLException;
}
