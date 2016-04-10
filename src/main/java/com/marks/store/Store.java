package com.marks.store;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class Store {

    private Morphia morphia = new Morphia();
    private Datastore datastore = null;
    private static Store store = null;

    private Store() {
        morphia.mapPackage("com.marks.model");
        datastore = createDatastore("marksDB");
    }

    private Store(String dbName) {
        morphia.mapPackage("com.marks.model");
        datastore = createDatastore(dbName);
    }

    public static synchronized Store getInstance() {
        if(store == null) {
            store = new Store();
        }
        return store;
    }

    public static Store getInstanceWithDBName(String dbName) {
        if(store == null) {
            store = new Store(dbName);
        }
        return store;
    }

    private Datastore createDatastore(String dbName) {
        datastore = morphia.createDatastore(new MongoClient(), dbName);
        datastore.ensureIndexes();
        return datastore;
    }

    public Datastore getDatastore() {
        return datastore;
    }


}
