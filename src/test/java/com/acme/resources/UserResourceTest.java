package com.acme.resources;

import com.acme.core.User;
import com.acme.db.UserDAO;
import com.acme.fixtures.UserFixtures;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests {@link io.dropwizard.testing.junit.ResourceTestRule}
 */
public class UserResourceTest {

    private static final UserDAO userDAO = mock(UserDAO.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new UserResource(userDAO))
            .build();

    static {
        Logger.getLogger("com.sun.jersey").setLevel(Level.OFF);
    }

    @After
    public void tearDown() {
        reset(userDAO);
    }

    @Test
    public void getAll() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User(1, "person1"));
        users.add(new User(2, "person2"));
        when(userDAO.getAll()).thenReturn(users);

        List<User> result = resources.client()
                .target("/users")
                .request()
                .get(new GenericType<List<User>>() {});

        assertEquals(2, result.size());
        assertEquals("person1", result.get(0).getName());
    }

    @Test
    public void get() throws Exception {
        when(userDAO.findById(1))
                .thenReturn(new User(1, "person1"));

        User user = resources.client()
                .target("/users/1")
                .request()
                .get(new GenericType<User>() {});

        assertEquals("person1", user.getName());
    }

    @Test
    public void update() throws Exception {
        User user = UserFixtures.getUser();

        User updatedUser = resources.client()
                .target("/users/10")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(user), User.class);

        assertEquals(user.getId(), updatedUser.getId());
        assertEquals(user.getName(), updatedUser.getName());

        verify(userDAO, times(1)).update(user);
    }

    @Test
    public void update_invalid_person() throws Exception {
        User user = UserFixtures.getUser();
        try {
            User updatedPerson = resources.client().target("/users/10")
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(user), User.class);
            fail("Should have thrown validation error");
        } catch (ClientErrorException ex) {
        }
    }

    @Test()
    public void add() throws Exception {
        User newUser = UserFixtures.getUser();

        User user = resources.client()
                .target("/users")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(newUser), User.class);

        assertEquals(newUser.getName(), user.getName());
        verify(userDAO, times(1)).insert(any(User.class));
    }

    @Test
    public void add_invalid_person() throws Exception {
        User newUser = new User(10, null);

        try {
            User user = resources.client().target("/users")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(newUser), User.class);
            fail("Should have thrown validation error");
        } catch (ClientErrorException ex) {
        }
    }

    @Test()
    public void delete() throws Exception {
        resources.client()
                .target("/users/1")
                .request()
                .delete();
        verify(userDAO, times(1)).deleteById(1);
    }
}
