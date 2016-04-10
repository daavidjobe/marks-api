package com.marks.store;

import com.marks.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.Key;

import static org.junit.Assert.assertNotNull;

public class StoreTest {

    Store store;

    @Before
    public void setUp() throws Exception {
        store = new Store();
        store.createDatastore("marksDB-test");
    }

    @After
    public void tearDown() throws Exception {
        store.getDatastore().getDB().dropDatabase();
    }

    @Test
    public void shouldSaveNewUserToCollection() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        Key<User> saved = store.getDatastore().save(user);
        assertNotNull(saved);
    }
}