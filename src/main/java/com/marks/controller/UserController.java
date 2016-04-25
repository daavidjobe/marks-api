package com.marks.controller;

import com.google.gson.Gson;
import com.marks.service.UserService;
import com.marks.util.JsonTransformer;
import org.apache.log4j.Logger;

import static spark.Spark.get;
import static spark.Spark.post;

public class UserController {

    private Gson gson = new Gson();
    public static final String BASE_PATH = "/api/users";
    private String jsonType = "application/json";

    Logger logger = Logger.getLogger(UserController.class);

    public UserController(UserService service) {

        get(BASE_PATH + "/findAll", jsonType, (req, res) -> {
            logger.info("fetching all users");
            res.type(jsonType);
            return service.getAllUsers();
        }, new JsonTransformer());

        post(BASE_PATH + "/add", jsonType, (req, res) -> {
            logger.info("adding user");
            return null;
        }, new JsonTransformer());
    }
}
