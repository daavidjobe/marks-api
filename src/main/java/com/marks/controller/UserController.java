package com.marks.controller;

import com.google.gson.Gson;
import com.marks.model.User;
import com.marks.service.UserService;
import com.marks.util.JsonTransformer;
import org.apache.log4j.Logger;
import org.mongodb.morphia.Key;

import static spark.Spark.*;

public class UserController {

    private Gson gson = new Gson();
    public static final String BASE_PATH = "/users";
    private String jsonType = "application/json";

    Logger logger = Logger.getLogger(UserController.class);

    public UserController(UserService service) {

        get(BASE_PATH + "/findAll", jsonType, (req, res) -> {
            logger.info("fetching all users");
            res.type(jsonType);
            return service.getAllUsers();
        }, new JsonTransformer());

        get(BASE_PATH + "/findByEmail/:email", jsonType, (req, res) -> {
            logger.info("fetching user by email");
            return service.getUserByEmail(req.params(":email"));
        }, new JsonTransformer());

        post(BASE_PATH + "/add", jsonType, (req, res) -> {
            logger.info("adding user");
            res.type(jsonType);
            User user = gson.fromJson(req.body(), User.class);
            Key<User> saved = service.add(user);
            return saved != null ? service.getUserByEmail(user.getEmail()) : "user could not be created";
        }, new JsonTransformer());

    }
}
