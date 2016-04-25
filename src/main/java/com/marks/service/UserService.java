package com.marks.service;


import com.marks.model.User;
import com.marks.store.Store;
import org.apache.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;

import java.util.List;

public class UserService {

    Datastore store = Store.getInstance().getDatastore();

    Logger logger = Logger.getLogger(UserService.class);


    public List<User> getAllUsers() {
        return store.createQuery(User.class)
                .asList();
    }

    public User getUserByField(String field) {
        User user = store.find(User.class).filter(field + " =", field).get();
        return user != null ? user : null;
    }

    public Key<User> add(User user) {
        if (store.find(User.class).filter("email =", user.getEmail()).asList().size() == 0) {
            return store.save(user);
        } else {
            return null;
        }
    }

    public Key<User> update(User user) {
        Query<User> query = store.createQuery(User.class).filter("email =", user.getEmail());
        User storedUser = query.get();
        return storedUser != null ? store.save(user) : null;
    }

}
