package com.revolut.revolutaccountmanager.resource;

import com.revolut.revolutaccountmanager.model.request.CreateAccountRequest;
import com.revolut.revolutaccountmanager.repository.AccountRepository;
import com.revolut.revolutaccountmanager.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("users/accounts")
public class AccountResource {

    private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Inject
    public AccountResource(AccountRepository accountRepository, AccountService accountService) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("create")
    public Response createAccountForUser(CreateAccountRequest createAccountRequest) {
        try {
            return Response.ok(accountRepository.createAccount(createAccountRequest)).build();
        } catch (Exception ex) {
            LOG.error("Failed to create account for the request: {}", createAccountRequest, ex);
            return Response.serverError().build();
        }
    }
}
