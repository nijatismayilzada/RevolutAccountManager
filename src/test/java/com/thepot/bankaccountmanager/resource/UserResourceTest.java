package com.thepot.bankaccountmanager.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepot.bankaccountmanager.model.request.CreateUserRequest;
import com.thepot.bankaccountmanager.model.user.User;
import com.thepot.bankaccountmanager.repository.UserRepository;
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

    @Test
    public void getUserById_whenUserIsAvailable_thenReturnUserDetails() {
        User user = new User();
        user.setName("TestUser");
        user.setUserId(USER_ID);

        when(userRepository.getUserById(USER_ID)).thenReturn(user);

        Response response = target("/users/user-id").path(String.valueOf(USER_ID)).request().get();

        assertThat("Http Response should be OK", response.getStatus(), is(OK.getStatusCode()));
        assertThat("Response should contain user details: " + user.toString(), response.readEntity(User.class), is(user));
    }

    @Test
    public void getUserById_whenRepositoryFailsToFetchUserDetails_thenRespondInternalServerError() {
        when(userRepository.getUserById(USER_ID)).thenThrow(new RuntimeException());

        Response response = target("/users/user-id").path(String.valueOf(USER_ID)).request().get();

        assertThat("Http Response should be INTERNAL_SERVER_ERROR", response.getStatus(), is(INTERNAL_SERVER_ERROR.getStatusCode()));
    }
}
