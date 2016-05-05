package com.marks.service;

import com.marks.model.Mark;
import com.marks.model.User;
import com.marks.store.Store;
import com.marks.util.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.Datastore;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by David Jobe on 4/25/16.
 */
public class MarkServiceTest {

    Datastore store;
    MarkService markService;
    UserService userService;
    Mark testMark;
    User testUser;

    @Before
    public void setUp() throws Exception {
        Store.initialize(Config.DB_TEST);
        markService = new MarkService();
        userService = new UserService();
        store = Store.getInstance().getDatastore();
        testMark = new Mark();
        testMark.setUrl("http://www.testing.com");
        store.save(testMark);
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
    public void findById() throws Exception {
        Mark stored = markService.findById(testMark.getId().toString());
        assertNotNull(stored);
    }



}