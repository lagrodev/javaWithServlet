package ru.hexaend.repository.jdbc.impl;

import ru.hexaend.repository.jdbc.DataSourceProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingleConnectionDataSourceProvider implements DataSourceProvider {
    private final String url;
    private final String username;
    private final String password;

    public SingleConnectionDataSourceProvider(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    public SingleConnectionDataSourceProvider(String url) {
        this(url, null, null);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (username != null) {
            return DriverManager.getConnection(url, username, password);
        }
        else {
            return DriverManager.getConnection(url);
        }
    }
}
