package com.marks.controller;

import com.google.gson.Gson;
import com.marks.model.Mark;
import com.marks.model.User;
import com.marks.service.MarkService;
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
    MarkService markService = new MarkService();
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

        post(BASE_PATH + "/addCategory", (req, res) -> {
            boolean isAdded = service.addCategory(req.queryParams("categoryName"), req.queryParams("email"));
            return isAdded == true ? "category added" : "category already exists";
        }, new JsonTransformer());

        put(BASE_PATH + "/addMarkToCategory", (req, res) -> {
            Mark mark = gson.fromJson(req.body(), Mark.class);
            String categoryName = req.queryParams("categoryName");
            String email = req.queryParams("email");
            boolean isOk = service.addToCategory(mark, email, categoryName);
            return isOk ? "mark added to" + categoryName : "mark could not be added to category";
        }, new JsonTransformer());

        get(BASE_PATH + "/findAllMarksForUser", (req, res) -> {
            return service.findAllMarksForUser(req.queryParams("email"));
        });

        get(BASE_PATH + "/findAllCategoriesForUser", (req, res) -> {
            return service.findAllCategoriesForUser(req.queryParams("email"));
        });

    }
}
