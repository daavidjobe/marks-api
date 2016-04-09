package com.marks.store;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class Store {

    private MongoClient mongoClient;
    private MongoDatabase dataBase;

    public Store(String dbName) {
        if(mongoClient == null) {
            mongoClient = new MongoClient("localhost" , 27017);
            dataBase = mongoClient.getDatabase(dbName);
        }
    }

    public MongoDatabase getDatabase() {
        return dataBase;
    }

    public void terminate() {
        mongoClient.close();
    }
}
