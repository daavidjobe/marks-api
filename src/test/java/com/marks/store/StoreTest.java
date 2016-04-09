package com.marks.store;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StoreTest {

    Store store;

    @Before
    public void setUp() {
        store = new Store("test-db");
    }

    @After
    public void cleanUp() {
        store.getDatabase().drop();
        store.terminate();
    }

    @Test
    public void shouldReturnCorrectDatabaseName() throws Exception {
        assertEquals("test-db", store.getDatabase().getName());
    }




}