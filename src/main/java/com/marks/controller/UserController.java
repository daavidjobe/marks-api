package com.marks.controller;

import com.google.gson.Gson;
import com.marks.model.User;
import com.marks.service.UserService;
import com.marks.util.Config;
import com.marks.util.JsonTransformer;
import org.apache.log4j.Logger;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public class UserController {

    private Gson gson = new Gson();
    public static final String BASE_PATH = Config.ROOT_PATH + "/users";

    Logger logger = Logger.getLogger(UserController.class);

    public UserController(UserService service) {

        get(BASE_PATH + "/findAll", (req, res) -> {
            logger.info("fetching all users");
            return service.getAllUsers();
        }, new JsonTransformer());

        get(BASE_PATH + "/findByEmail/:email", (req, res) -> {
            logger.info("fetching user by email");
            return service.getUserByEmail(req.params(":email"));
        }, new JsonTransformer());

        post(BASE_PATH + "/signup", (req, res) -> {;
            User user = gson.fromJson(req.body(), User.class);
            logger.info(user);
            boolean isAdded = service.signup(user);
            if(!isAdded) {
                res.status(406);
                return "user could not be created";
            }
            res.cookie("mrksusrid", user.getEmail(), 2592000, true); // one month
            res.status(201);
            return "user created";
        }, new JsonTransformer());

        post(BASE_PATH + "/login", (req, res) -> {
            User user = gson.fromJson(req.body(), User.class);
            if(service.login(user.getEmail(), user.getPassword()) == null) {
                res.status(406);
                return "login failed";
            }
            res.status(200);
            return "login success";
        }, new JsonTransformer());

        put(BASE_PATH + "/addMark", (req, res) -> {
            return service.addMark(req.queryParams("url"), req.queryParams("userEmail"));
        }, new JsonTransformer());

    }
}
