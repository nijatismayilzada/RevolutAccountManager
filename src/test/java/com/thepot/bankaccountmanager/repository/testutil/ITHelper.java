package com.thepot.bankaccountmanager.repository.testutil;

import com.thepot.bankaccountmanager.config.JdbcConnection;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ITHelper {

    private final JdbcConnection jdbcConnection;

    public ITHelper(JdbcConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }

    public long createUser(String name) throws SQLException {

        PreparedStatement statement = jdbcConnection.getConnection().prepareStatement("insert into user(name) values(?)", Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, name);
        statement.executeUpdate();
        ResultSet resultSet = statement.getGeneratedKeys();
        resultSet.next();
        return resultSet.getLong(1);

    }

    public long createAccount(long userId, String currency, BigDecimal balance) throws SQLException {

        PreparedStatement statement = jdbcConnection.getConnection().prepareStatement("insert into account(user_id, currency, balance) values(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setLong(1, userId);
        statement.setString(2, currency);
        statement.setBigDecimal(3, balance);
        statement.executeUpdate();
        ResultSet resultSet = statement.getGeneratedKeys();
        resultSet.next();
        return resultSet.getLong(1);

    }

    public ResultSet getAccount(long accountId) throws SQLException {

        PreparedStatement statement = jdbcConnection.getConnection().prepareStatement("select account_id, user_id, currency, balance from account where account_id = ?");
        statement.setLong(1, accountId);

        return statement.executeQuery();

    }

    public ResultSet getUser(long userId) throws SQLException {
        PreparedStatement statement = jdbcConnection.getConnection().prepareStatement("select user_id, name from user where user_id = ?");
        statement.setLong(1, userId);

        return statement.executeQuery();
    }
}
