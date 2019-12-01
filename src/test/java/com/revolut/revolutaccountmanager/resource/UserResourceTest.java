package com.revolut.revolutaccountmanager.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.revolutaccountmanager.model.request.CreateUserRequest;
import com.revolut.revolutaccountmanager.repository.UserRepository;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserResourceTest extends JerseyTest {

    private static final long USER_ID = -111L;
    private UserRepository userRepository;
    private CreateUserRequest createUserRequest;

    @Override
    protected Application configure() {
        userRepository = mock(UserRepository.class);
        return new ResourceConfig().register(new UserResource(userRepository));
    }

    @Before
    public void setup() {
        createUserRequest = new CreateUserRequest();
        createUserRequest.setName("TestUser");
    }

    @Test
    public void createUser_whenCreateUserRequested_thenCreateNewUserId() throws JsonProcessingException {
        when(userRepository.createUser(createUserRequest)).thenReturn(USER_ID);

        Response response = target("/users/create").request()
                .post(Entity.json(new ObjectMapper().writeValueAsString(createUserRequest)));

        assertThat("Http Response should be OK", response.getStatus(), is(OK.getStatusCode()));
        assertThat("Response should contain user id " + USER_ID, response.readEntity(String.class), is(String.valueOf(USER_ID)));
    }

    @Test
    public void createUser_whenRepositoryFails_thenRespondInternalServerError() throws JsonProcessingException {
        when(userRepository.createUser(createUserRequest)).thenThrow(new RuntimeException());

        Response response = target("/users/create").request()
                .post(Entity.json(new ObjectMapper().writeValueAsString(createUserRequest)));

        assertThat("Http Response should be INTERNAL_SERVER_ERROR", response.getStatus(), is(INTERNAL_SERVER_ERROR.getStatusCode()));
    }
}
