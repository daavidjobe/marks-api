package com.marks.service;

import com.marks.model.User;
import com.marks.store.Store;
import com.marks.util.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNull;


public class UserServiceTest {

    Datastore store;
    UserService userService;

    @Before
    public void setUp() throws Exception {
        Store.initialize(Config.DB_TEST);
        userService = new UserService();
        store = Store.getInstance().getDatastore();
    }

    @After
    public void tearDown() throws Exception {
        store.getDB().dropDatabase();
    }

    @Test
    public void gettingUserByEmail() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        assertNull(userService.getUserByEmail("test@test.com"));
        store.save(user);
        assertNotNull(userService.getUserByEmail("test@test.com"));
    }

    @Test
    public void newUserShouldBeCreated() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("Testing123");
        Key<User> saved = userService.add(user);
        assertNotNull(saved);
    }

    @Test
    public void sameUserCanNotBeCreatedAgain() throws Exception {
        User user1 = new User();
        user1.setEmail("test@test.com");
        user1.setPassword("Testing123");
        Key<User> first = userService.add(user1);
        User user2 = new User();
        user2.setEmail("test@test.com");
        user2.setPassword("Testing123");
        Key<User> second = userService.add(user2);
        assertNotNull(first);
        assertNull(second);
    }

    @Test
    public void userMustHaveValidEmail() throws Exception {
        User user = new User();
        user.setEmail("test@");
        user.setPassword("Testing123");
        assertNull(userService.add(user));
    }

    @Test
    public void storedUserCanLogin() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("Testing123");
        userService.add(user);
        User loggedIn = userService.login("test@test.com", "Testing123");
        assertNotNull(loggedIn);
    }

}