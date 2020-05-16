package com.thepot.bankaccountmanager.repository;

import com.thepot.bankaccountmanager.config.JdbcConnection;
import com.thepot.bankaccountmanager.model.account.Account;
import com.thepot.bankaccountmanager.model.request.CreateAccountRequest;
import com.thepot.bankaccountmanager.repository.testutil.ITHelper;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AccountRepositoryIT {
    private static final String TEST_CURRENCY = "GBP";
    private AccountRepository accountRepository;
    private ITHelper itHelper;
    private long testUserId;

    @Before
    public void setUp() throws Exception {
        JdbcConnection jdbcConnection = new JdbcConnection();
        accountRepository = new AccountRepository(jdbcConnection);
        itHelper = new ITHelper(jdbcConnection);
        testUserId = itHelper.createUser("TestUser");
    }

    @Test
    public void createAccount_givenAccountDetails_canCreateNewAccountWithId() throws SQLException {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setUserId(testUserId);
        createAccountRequest.setCurrency(Currency.getInstance(TEST_CURRENCY));

        long accountId = accountRepository.createAccount(createAccountRequest);

        ResultSet resultSet = itHelper.getAccount(accountId);
        assertThat(resultSet.next(), is(true));
        assertThat(resultSet.getLong("user_id"), is(testUserId));
        assertThat(resultSet.getLong("account_id"), is(accountId));
        assertThat(resultSet.getString("currency"), is(TEST_CURRENCY));
        assertThat(resultSet.getBigDecimal("balance").compareTo(BigDecimal.ZERO), is(0));
    }

    @Test
    public void getAccount_givenExistingAccount_canFetchThatAccount() throws SQLException {
        long testAccountId = itHelper.createAccount(testUserId, TEST_CURRENCY, BigDecimal.ONE);

        Account account = accountRepository.getAccount(testAccountId);

        assertThat(account.getAccountId(), is(testAccountId));
        assertThat(account.getBalance().compareTo(BigDecimal.ONE), is(0));
        assertThat(account.getCurrency(), is(Currency.getInstance(TEST_CURRENCY)));
    }

    @Test
    public void increaseAccountBalance_givenExistingAccount_canIncreaseItsBalance() throws SQLException {
        long testAccountId = itHelper.createAccount(testUserId, TEST_CURRENCY, BigDecimal.ONE);

        accountRepository.increaseAccountBalance(testAccountId, BigDecimal.TEN);

        ResultSet resultSet = itHelper.getAccount(testAccountId);
        assertThat(resultSet.next(), is(true));
        assertThat(resultSet.getBigDecimal("balance").compareTo(new BigDecimal(11)), is(0));
    }

    @Test
    public void decreaseAccountBalance_givenExistingAccount_canDecreaseItsBalance() throws SQLException {
        long testAccountId = itHelper.createAccount(testUserId, TEST_CURRENCY, BigDecimal.TEN);

        accountRepository.decreaseAccountBalance(testAccountId, BigDecimal.ONE);

        ResultSet resultSet = itHelper.getAccount(testAccountId);
        assertThat(resultSet.next(), is(true));
        assertThat(resultSet.getBigDecimal("balance").compareTo(new BigDecimal(9)), is(0));
    }
}
