package com.marks.controller;

import com.google.gson.Gson;
import com.marks.model.User;
import com.marks.store.Store;
import com.marks.util.JsonTransformer;
import org.apache.log4j.Logger;
import org.mongodb.morphia.Key;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;

public class UserController {

    private Gson gson = new Gson();
    private Store store = Store.getInstance();
    public static final String BASE_PATH = "/users";
    private String jsonType = "application/json";

    Logger logger = Logger.getLogger(UserController.class);

    public void setupEndpoints() {

        post(BASE_PATH + "/add", jsonType, (req, res) -> {
            res.type(jsonType);
            User user = gson.fromJson(req.body(), User.class);
            User exists = store.getDatastore().createQuery(User.class)
                    .field("email").equal(user.getEmail()).limit(1).get();
            if(exists != null) {
                res.status(409);
                return "user already signed up";
            }
            logger.info("new: " + user);
            Key<User> saved = store.getDatastore().save(user);
            logger.info("saved: " + saved);
            res.status(201);
            return user;
        }, new JsonTransformer());

        get(BASE_PATH + "/findAll", jsonType, (req, res) -> {
            res.type(jsonType);
            List<User> users = store.getDatastore().createQuery(User.class)
                    .asList();
            return users;
        }, new JsonTransformer());

    }
}
