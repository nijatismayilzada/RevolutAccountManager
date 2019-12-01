package com.revolut.revolutaccountmanager.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.revolutaccountmanager.model.request.CreateAccountRequest;
import com.revolut.revolutaccountmanager.repository.AccountRepository;
import com.revolut.revolutaccountmanager.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.Currency;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountResourceTest extends JerseyTest {
    private static final long ACCOUNT_ID = -111L;
    private static final String CURRENCY_CODE = "GBP";
    private static final int USER_ID = -999;

    private AccountRepository accountRepository;
    private AccountService accountService;
    private CreateAccountRequest createAccountRequest;

    @Override
    protected Application configure() {
        accountRepository = mock(AccountRepository.class);
        accountService = mock(AccountService.class);
        return new ResourceConfig().register(new AccountResource(accountRepository, accountService));
    }

    @Before
    public void setup() {
        createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setUserId(USER_ID);
        createAccountRequest.setCurrency(Currency.getInstance(CURRENCY_CODE));

    }

    @Test
    public void createAccountForUser_whenCreateAccountRequestPosted_thenCreateNewAccountId() throws JsonProcessingException {
        when(accountRepository.createAccount(createAccountRequest)).thenReturn(ACCOUNT_ID);

        Response response = target("/users/accounts/create").request()
                .post(Entity.json(new ObjectMapper().writeValueAsString(createAccountRequest)));

        assertThat("Http Response should be OK", response.getStatus(), is(OK.getStatusCode()));
        assertThat("Response should contain account id " + ACCOUNT_ID, response.readEntity(String.class), is(String.valueOf(ACCOUNT_ID)));
    }

    @Test
    public void createAccountForUser_whenRepositoryFails_thenRespondInternalServerError() throws JsonProcessingException {
        when(accountRepository.createAccount(createAccountRequest)).thenThrow(new RuntimeException());
        Response response = target("/users/accounts/create").request()
                .post(Entity.json(new ObjectMapper().writeValueAsString(createAccountRequest)));

        assertThat("Http Response should be INTERNAL_SERVER_ERROR", response.getStatus(), is(INTERNAL_SERVER_ERROR.getStatusCode()));
    }
}