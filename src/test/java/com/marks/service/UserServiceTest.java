package com.marks.service;

import com.marks.model.Mark;
import com.marks.model.User;
import com.marks.store.Store;
import com.marks.util.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.VerboseJSR303ConstraintViolationException;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


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
    public void newUserShouldBeCreated() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        Key<User> saved = userService.add(user);
        assertNotNull(saved);
    }

    @Test
    public void sameUserCanNotBeCreatedAgain() throws Exception {
        User user1 = new User();
        user1.setEmail("test@test.com");
        Key<User> first = userService.add(user1);
        User user2 = new User();
        user2.setEmail("test@test.com");
        Key<User> second = userService.add(user2);
        assertNotNull(first);
        assertNull(second);
    }

    @Test(expected = VerboseJSR303ConstraintViolationException.class)
    public void userMustHaveValidEmail() throws Exception {
        User user = new User();
        user.setEmail("test@");
        userService.add(user);

    }

    @Test
    public void userShouldBeUpdated() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        userService.add(user);
        user.setFirstName("tester");
        assertTrue(user.getVersion() == 1);
        Key<User> updated = userService.update(user);
        User saved = store.getByKey(User.class, updated);
        assertTrue(saved.getVersion() == 2);
    }

    @Test
    public void newMarkShouldBeAdded() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        userService.add(user);
        Mark mark = new Mark();
        mark.setUrl("http://google.com");
        user.getMarks().add(mark);
        Key<User> updated = userService.update(user);
        User stored = store.getByKey(User.class, updated);
        System.out.println(stored);
        assertTrue(stored.getMarks().size() == 1);

    }

}