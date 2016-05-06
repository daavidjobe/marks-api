package com.marks.controller;

import com.google.gson.Gson;
import com.marks.model.Mark;
import com.marks.model.User;
import com.marks.service.UserService;
import com.marks.util.Config;
import org.apache.log4j.Logger;

import static spark.Spark.*;

public class UserController {

    private Gson gson = new Gson();
    public static final String BASE_PATH = Config.ROOT_PATH + "/users";
    Logger logger = Logger.getLogger(UserController.class);

    public UserController(UserService service) {

        get(BASE_PATH + "/findAll", (req, res) -> {
            logger.info("fetching all users");
            return service.getAllUsers();
        }, gson::toJson);

        get(BASE_PATH + "/findByEmail/:email", (req, res) -> {
            logger.info("fetching user by email");
            return service.getUserByEmail(req.params(":email"));
        }, gson::toJson);

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
        }, gson::toJson);

        post(BASE_PATH + "/login", (req, res) -> {
            User user = gson.fromJson(req.body(), User.class);
            if(service.login(user.getEmail(), user.getPassword()) == null) {
                res.status(406);
                return "login failed";
            }
            res.status(200);
            return "login success";
        }, gson::toJson);

        post(BASE_PATH + "/addCategory/:name", (req, res) -> {
            boolean isAdded = service.addCategory(req.params(":name"), req.queryParams("email"));
            return isAdded == true ? "category added" : "category already exists";
        }, gson::toJson);

        post(BASE_PATH + "/removeCategory/:name", (req, res) -> {
            boolean isRemoved = service.removeCategory(req.params(":name"), req.queryParams("email"));
            return isRemoved == true ? "category removed" : "category could not be removed";
        }, gson::toJson);

        put(BASE_PATH + "/addMarkToCategory", (req, res) -> {
            Mark mark = gson.fromJson(req.body(), Mark.class);
            String categoryName = req.queryParams("categoryName");
            String email = req.queryParams("email");
            boolean isOk = service.addToCategory(mark.getUrl(), email, categoryName);
            return isOk ? "mark added to" + categoryName : "mark could not be added to category";
        }, gson::toJson);

        get(BASE_PATH + "/findAllMarksForUser", (req, res) -> {
            return service.findAllMarksForUser(req.queryParams("email"));
        });

        get(BASE_PATH + "/findAllCategoriesForUser", "application/json", (req, res) -> {
            res.type("application/json");
            return service.findAllCategoriesForUser(req.queryParams("email"));
        }, gson::toJson);

    }
}
