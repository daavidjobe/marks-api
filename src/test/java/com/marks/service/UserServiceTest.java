package com.marks.service;

import com.marks.model.Category;
import com.marks.model.User;
import com.marks.store.Store;
import com.marks.util.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.Datastore;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class UserServiceTest {

    Datastore store;
    UserService userService;
    MarkService markService;
    User testUser;

    @Before
    public void setUp() throws Exception {
        Store.initialize(Config.DB_TEST);
        userService = new UserService();
        markService = new MarkService();
        store = Store.getInstance().getDatastore();
        testUser = new User();
        testUser.setEmail("tester@tester.com");
        testUser.setPassword("Testing123");
        userService.signup(testUser);
    }

    @After
    public void tearDown() throws Exception {
        store.getDB().dropDatabase();
    }

    @Test
    public void getUserByEmail() throws Exception {
        assertNotNull(userService.getUserByEmail(testUser.getEmail()));
    }

    @Test
    public void newUserShouldBeCreated() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("Testing123");
        assertTrue(userService.signup(user));
    }

    @Test
    public void sameUserCanNotBeCreatedAgain() throws Exception {
        User user1 = new User();
        user1.setEmail("test@test.com");
        user1.setPassword("Testing123");
        User user2 = new User();
        user2.setEmail("test@test.com");
        user2.setPassword("Testing123");
        assertTrue(userService.signup(user1));
        assertFalse(userService.signup(user2));
    }

    @Test
    public void userMustHaveValidEmail() throws Exception {
        User user = new User();
        user.setEmail("test@");
        user.setPassword("Testing123");
        assertFalse(userService.signup(user));
    }

    @Test
    public void storedUserCanLogin() throws Exception {
        User user = new User();
        user.setEmail("tester42@test.com");
        user.setPassword("Password123");
        userService.signup(user);
        User loggedIn = userService.login(user.getEmail(), "Password123");
        assertNotNull(loggedIn);
    }

    @Test
    public void addCategory() throws Exception {
        assertTrue(userService.addCategory("tech", testUser.getEmail()));
    }

    @Test
    public void removeCategory() throws Exception {
        assertTrue(userService.addCategory("tech", testUser.getEmail()));
        assertTrue(userService.removeCategory("tech", testUser.getEmail()));
        assertTrue(testUser.getCategories().size() == 0);
    }

    @Test
    public void addMarkToCategory() throws Exception {
        markService.addMark("https://timebeat.com", testUser.getEmail());
        assertTrue(userService.addCategory("cat", testUser.getEmail()));
        assertTrue(userService.addToCategory("https://timebeat.com", testUser.getEmail(), "cat"));
        User usr = userService.getUserByEmail(testUser.getEmail());
        System.out.println(usr);
        assertTrue(usr.getCategories().get(0).getUrls().size() == 1);
        markService.addMark("https://enigio.com", testUser.getEmail());
        assertTrue(userService.addToCategory("https://enigio.com", testUser.getEmail(), "cat"));
        usr = userService.getUserByEmail(testUser.getEmail());
        System.out.println(usr);
        assertTrue(usr.getCategories().get(0).getUrls().size() == 2);
    }

    @Test
    public void removeMarkFromCategory() throws Exception {
        markService.addMark("https://timebeat.com", testUser.getEmail());
        userService.addCategory("cat", testUser.getEmail());
        userService.addToCategory("https://timebeat.com", testUser.getEmail(), "cat");
        User usr = userService.getUserByEmail(testUser.getEmail());
        Category category = usr.getCategories().stream().filter(cat -> cat.getName().equals("cat")).findFirst().get();
        assertTrue(category.getUrls().size() == 1);
        userService.removeFromCategory("https://timebeat.com", testUser.getEmail(), "cat");
        usr = userService.getUserByEmail(testUser.getEmail());
        category = usr.getCategories().stream().filter(cat -> cat.getName().equals("cat")).findFirst().get();
        assertTrue(category.getUrls().size() == 0);
    }

    @Test
    public void ifMarkIsAlreadyAddedToCategoryThenReturnFalse() throws Exception {
        markService.addMark("https://timebeat.com", testUser.getEmail());
        assertTrue(userService.addCategory("cat", testUser.getEmail()));
        assertTrue(userService.addToCategory("https://timebeat.com", testUser.getEmail(), "cat"));
        assertFalse(userService.addToCategory("https://timebeat.com", testUser.getEmail(), "cat"));
    }

    @Test
    public void ifMarkIsMovedToAnotherCategoryItShouldBeRemovedFirst() throws Exception {
        markService.addMark("https://timebeat.com", testUser.getEmail());
        assertTrue(userService.addCategory("cat", testUser.getEmail()));
        assertTrue(userService.addToCategory("https://timebeat.com", testUser.getEmail(), "cat"));
        assertTrue(userService.addCategory("cat2", testUser.getEmail()));
        assertTrue(userService.addToCategory("https://timebeat.com", testUser.getEmail(), "cat2"));
        User usr = userService.getUserByEmail(testUser.getEmail());
        System.out.println(usr);
        Category category = usr.getCategories().stream()
                .filter(c -> c.getName().equals("cat")).findFirst().get();

        assertFalse(category.getUrls().contains("https://timebeat.com"));
        assertTrue(category.getUrls().size() == 0);

    }



}