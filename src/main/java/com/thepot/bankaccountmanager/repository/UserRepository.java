package com.thepot.bankaccountmanager.repository;

import com.thepot.bankaccountmanager.config.JdbcConnection;
import com.thepot.bankaccountmanager.model.account.Account;
import com.thepot.bankaccountmanager.model.exception.SQLRuntimeException;
import com.thepot.bankaccountmanager.model.request.CreateUserRequest;
import com.thepot.bankaccountmanager.model.user.User;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Service
public class UserRepository {
    private static final String INSERT_USER = "insert into user(name) values(?)";
    private static final String SELECT_USER_WITH_ACCOUNTS = "select u.user_id, u.name, " +
            "a.account_id, a.currency, a.balance " +
            "from user u left join account a on u.user_id = a.user_id where u.user_id = ?";

    private final JdbcConnection jdbcConnection;

    @Inject
    public UserRepository(JdbcConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }

    public long createUser(CreateUserRequest createUserRequest) {
        try {
            PreparedStatement statement = jdbcConnection.getConnection().prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, createUserRequest.getName());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public User getUserById(long userId) {
        try {
            PreparedStatement statement = jdbcConnection.getConnection().prepareStatement(SELECT_USER_WITH_ACCOUNTS);
            statement.setLong(1, userId);

            ResultSet resultSet = statement.executeQuery();

            User user = new User();
            user.setUserId(userId);
            List<Account> accounts = new ArrayList<>();
            user.setAccounts(accounts);

            while (resultSet.next()) {
                user.setName(resultSet.getString("name"));
                Account account = new Account();
                account.setAccountId(resultSet.getLong("account_id"));
                if (account.getAccountId() != 0) {
                    account.setBalance(resultSet.getBigDecimal("balance"));
                    account.setCurrency(Currency.getInstance(resultSet.getString("currency")));
                    accounts.add(account);
                }
            }

            return user;

        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }
}
