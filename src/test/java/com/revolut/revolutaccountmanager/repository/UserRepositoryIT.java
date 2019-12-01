package com.revolut.revolutaccountmanager.repository;

import com.revolut.revolutaccountmanager.config.JdbcConnection;
import com.revolut.revolutaccountmanager.model.request.CreateUserRequest;
import com.revolut.revolutaccountmanager.model.user.User;
import com.revolut.revolutaccountmanager.repository.testutil.ITHelper;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserRepositoryIT {
    private static final String TEST_USER = "TestUser";
    private UserRepository userRepository;
    private ITHelper itHelper;

    @Before
    public void setUp() throws Exception {
        JdbcConnection jdbcConnection = new JdbcConnection();
        userRepository = new UserRepository(jdbcConnection);
        itHelper = new ITHelper(jdbcConnection);
    }

    @Test
    public void createUser_givenName_canCreateNewUser() throws SQLException {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName(TEST_USER);

        long testUserId = userRepository.createUser(createUserRequest);

        ResultSet resultSet = itHelper.getUser(testUserId);
        assertThat(resultSet.next(), is(true));
        assertThat(resultSet.getLong("user_id"), is(testUserId));
        assertThat(resultSet.getString("name"), is(TEST_USER));
    }

    @Test
    public void getUserById_givenExistingUser_canFetchTheUser() throws SQLException {

        long testUserId = itHelper.createUser(TEST_USER);
        long testAccountId = itHelper.createAccount(testUserId, "GBP", BigDecimal.ONE);

        User user = userRepository.getUserById(testUserId);

        assertThat(user.getUserId(), is(testUserId));
        assertThat(user.getName(), is(TEST_USER));
        assertThat(user.getAccounts().size(), is(1));
        assertThat(user.getAccounts().get(0).getAccountId(), is(testAccountId));
        assertThat(user.getAccounts().get(0).getCurrency().getCurrencyCode(), is("GBP"));
        assertThat(user.getAccounts().get(0).getBalance().compareTo(BigDecimal.ONE), is(0));
    }
}
