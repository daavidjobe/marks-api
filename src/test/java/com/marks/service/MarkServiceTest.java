package com.marks.service;

import com.marks.store.Store;
import com.marks.util.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.Datastore;

/**
 * Created by Me on 4/25/16.
 */
public class MarkServiceTest {

    Datastore store;
    MarkService markService;

    @Before
    public void setUp() throws Exception {
        Store.initialize(Config.DB_TEST);
        markService = new MarkService();
        store = Store.getInstance().getDatastore();
    }

    @After
    public void tearDown() throws Exception {
        store.getDB().dropDatabase();
    }

    @Test
    public void findAll() throws Exception {

    }

    @Test
    public void findByUrl() throws Exception {

    }

    @Test
    public void add() throws Exception {

    }

    @Test
    public void incrementPopularityLevel() throws Exception {

    }

    @Test
    public void dencrementPopularityLevel() throws Exception {

    }

    @Test
    public void setPublished() throws Exception {

    }

}