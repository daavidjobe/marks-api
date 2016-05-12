package com.marks.store;

import com.marks.model.Mark;
import com.marks.model.MarkMeta;
import com.marks.model.User;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.apache.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.ValidationExtension;


public class Store {

    private Morphia morphia = new Morphia();
    private Datastore datastore = null;
    private static Store store = null;
    private static String DB_NAME;

    static Logger logger = Logger.getLogger(Store.class);

    public static void initialize(String dbName) {
        DB_NAME = dbName;
    }

    private Store() {
        morphia.map(User.class, Mark.class, MarkMeta.class);
        MongoClient client;
        String connectionUri = System.getenv("MONGODB_URI");
        if(connectionUri != null) {
            client = new MongoClient(new MongoClientURI(connectionUri));
        } else {
            client = new MongoClient();
        }
        datastore = morphia.createDatastore(client, DB_NAME);
        datastore.ensureIndexes();
        new ValidationExtension(morphia);
        logger.info("new Store created " + DB_NAME);
    }

    public static synchronized Store getInstance() {
        if(store == null) {
            store = new Store();
        }
        return store;
    }


    public Datastore getDatastore() {
        return datastore;
    }


}
