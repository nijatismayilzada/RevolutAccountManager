package com.revolut.revolutaccountmanager.config;

import org.jvnet.hk2.annotations.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class JdbcConnection {
    private final Connection connection;

    public JdbcConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:test;" +
                "MODE=MYSQL;" +
                "INIT=runscript from 'classpath:create.sql'", "sa", "");
    }

    public Connection getConnection() {
        return connection;
    }
}
