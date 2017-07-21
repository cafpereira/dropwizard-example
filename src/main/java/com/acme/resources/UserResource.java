package com.acme.resources;

import com.acme.core.User;
import com.acme.db.UserDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GET
    @UnitOfWork
    public List<User> findAll() {
        return userDAO.getAll();
    }

    @GET
    @Path("/{userId}")
    @UnitOfWork
    public User getUser(@PathParam("userId") Integer userId) {
        return userDAO.findById(userId);
    }

    @POST
    @UnitOfWork
    public User create(User user) {
        userDAO.insert(user);
        return user;
    }

    @PUT
    @Path("/{userId}")
    @UnitOfWork
    public User update(@PathParam("userId") Integer userId, User user) {
        userDAO.update(user);
        return user;
    }

    @DELETE
    @Path("/{userId}")
    @UnitOfWork
    public Response delete(@PathParam("userId") Integer userId) {
        userDAO.deleteById(userId);
        return Response.accepted().build();
    }
}
