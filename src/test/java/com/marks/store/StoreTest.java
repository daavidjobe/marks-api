package com.marks.store;

import com.marks.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.Key;

import static org.junit.Assert.*;

public class StoreTest {

    Store store = Store.getInstanceWithDBName("marksDB-test");

    @Before
    public void setUp() throws Exception {

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