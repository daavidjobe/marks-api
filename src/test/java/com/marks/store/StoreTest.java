package com.marks.store;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StoreTest {

    Store store;

    @Before
    public void setUp() {
        store = new Store("marksDB-test");
    }

    @After
    public void cleanUp() {
        store.getDatabase().drop();
        store.terminate();
    }

    @Test
    public void shouldReturnCorrectDatabaseName() throws Exception {
        assertEquals("marksDB-test", store.getDatabase().getName());
    }

    @Test
    public void shouldInsertDocument() throws Exception {
        MongoCollection<Document> collection = store.getDatabase().getCollection("test");
        Document doc = new Document("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("info", new Document("x", 203).append("y", 102));
        collection.insertOne(doc);
        assertEquals(collection.count(), 1);
    }




}