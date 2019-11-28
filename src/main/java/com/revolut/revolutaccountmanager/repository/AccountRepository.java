package com.revolut.revolutaccountmanager.repository;

import com.revolut.revolutaccountmanager.config.JdbcConnection;
import com.revolut.revolutaccountmanager.exception.SQLRuntimeException;
import com.revolut.revolutaccountmanager.model.CreateAccountRequest;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class AccountRepository {
    private static final String INSERT_ACCOUNT = "insert into account(user_id, currency, balance) values(?, ?, ?)";

    private final JdbcConnection jdbcConnection;

    @Inject
    public AccountRepository(JdbcConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }

    public long createAccount(CreateAccountRequest createAccountRequest) {
        try {
            PreparedStatement statement = jdbcConnection.getConnection().prepareStatement(INSERT_ACCOUNT, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, createAccountRequest.getUserId());
            statement.setString(2, createAccountRequest.getCurrency().getCurrencyCode());
            statement.setBigDecimal(3, BigDecimal.ZERO);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }
}
