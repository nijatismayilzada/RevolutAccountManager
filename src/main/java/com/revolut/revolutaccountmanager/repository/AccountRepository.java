package com.revolut.revolutaccountmanager.repository;

import com.revolut.revolutaccountmanager.config.JdbcConnection;
import com.revolut.revolutaccountmanager.model.account.Account;
import com.revolut.revolutaccountmanager.model.exception.SQLRuntimeException;
import com.revolut.revolutaccountmanager.model.exception.TransactionRuntimeException;
import com.revolut.revolutaccountmanager.model.request.CreateAccountRequest;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Currency;

@Service
public class AccountRepository {
    private static final String INSERT_ACCOUNT = "insert into account(user_id, currency, balance) values(?, ?, ?)";
    private static final String SELECT_ACCOUNT_BY_ID = "select account_id, user_id, currency, balance from account where account_id = ?";
    private static final String INCREASE_BALANCE_BY_AMOUNT = "update account set balance = balance + ? where account_id = ?";
    private static final String DECREASE_BALANCE_BY_AMOUNT = "update account set balance = balance - ? where account_id = ?";

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

    public Account getAccount(long accountId) {

        try {
            PreparedStatement statement = jdbcConnection.getConnection().prepareStatement(SELECT_ACCOUNT_BY_ID);
            statement.setLong(1, accountId);

            ResultSet resultSet = statement.executeQuery();

            Account account = new Account();

            if (resultSet.next()) {
                account.setAccountId(resultSet.getLong("account_id"));
                account.setCurrency(Currency.getInstance(resultSet.getString("currency")));
                account.setBalance(resultSet.getBigDecimal("balance"));
            }

            return account;

        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public void increaseAccountBalance(long accountId, BigDecimal byAmount) {
        try {
            PreparedStatement statement = jdbcConnection.getConnection().prepareStatement(INCREASE_BALANCE_BY_AMOUNT);
            statement.setBigDecimal(1, byAmount);
            statement.setLong(2, accountId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected != 1) {
                throw new TransactionRuntimeException(String.format("Could not increase account %s by %s amount. Actual row counts: %s", accountId, byAmount, rowsAffected));
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public void decreaseAccountBalance(long accountId, BigDecimal byAmount) {
        try {
            PreparedStatement statement = jdbcConnection.getConnection().prepareStatement(DECREASE_BALANCE_BY_AMOUNT);
            statement.setBigDecimal(1, byAmount);
            statement.setLong(2, accountId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected != 1) {
                throw new TransactionRuntimeException(String.format("Could not decrease account %s by %s amount. Actual row counts: %s", accountId, byAmount, rowsAffected));
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }
}
