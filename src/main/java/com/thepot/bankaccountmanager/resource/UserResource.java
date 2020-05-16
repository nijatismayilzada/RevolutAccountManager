package com.thepot.bankaccountmanager.resource;

import com.thepot.bankaccountmanager.model.request.CreateUserRequest;
import com.thepot.bankaccountmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("users")
public class UserResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    private final UserRepository userRepository;

    @Inject
    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("create")
    public Response createUser(CreateUserRequest createUserRequest) {
        try {
            return Response.ok(userRepository.createUser(createUserRequest)).build();
        } catch (Exception ex) {
            LOG.error("Failed to create user for the request: {}", createUserRequest, ex);
            return Response.serverError().build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user-id/{userId}")
    public Response getUserById(@PathParam("userId") long userId) {
        try {
            return Response.ok(userRepository.getUserById(userId)).build();
        } catch (Exception ex) {
            LOG.error("Failed to get user for id: {}", userId, ex);
            return Response.serverError().build();
        }
    }
}
