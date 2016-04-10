package com.marks.store;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class Store {

    private Morphia morphia = new Morphia();
    private Datastore datastore = null;

    public Store() {
        morphia.mapPackage("com.marks.model");
    }

    public Datastore createDatastore(String dbName) {
        datastore = morphia.createDatastore(new MongoClient(), dbName);
        datastore.ensureIndexes();
        return datastore;
    }

    public Datastore getDatastore() {
        return datastore;
    }


}
