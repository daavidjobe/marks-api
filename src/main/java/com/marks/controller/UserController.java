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

        before((req, res) -> {
            String method = req.requestMethod();
            if(method.equals("POST") || method.equals("PUT") || method.equals("DELETE")){
                String Authorization = req.headers("Authorization");
                if(!Config.AUTH.equals(Authorization)){
                    halt(401, "User Unauthorized");
                }
            }
        });

        /**
         * @return list of all users
         * @see User
         */
        get(BASE_PATH + "/findAll", (req, res) -> {
            logger.info("fetching all users");
            return service.getAllUsers();
        }, gson::toJson);

        /**
         * @body User
         * @return user specified bu email
         * @see User
         */
        get(BASE_PATH + "/findByEmail/:email", (req, res) -> {
            logger.info("fetching user by email");
            return service.getUserByEmail(req.params(":email"));
        }, gson::toJson);

        /**
         * @body User
         * @return User
         */
        post(BASE_PATH + "/signin", (req, res) -> {;
            User user = gson.fromJson(req.body(), User.class);
            return service.signin(user);
        }, gson::toJson);

        /**
         * Adds a category wich enables User to organize marks
         * @param name
         * @param email
         * @return message telling if the category was added or not
         * @see Category, Mark
         */
        post(BASE_PATH + "/addCategory/:name", (req, res) -> {
            boolean isAdded = service.addCategory(req.params(":name"), req.queryParams("email"));
            return isAdded == true ? "category added" : "category already exists";
        }, gson::toJson);

        /**
         * Removes a category specified by name
         * @param name
         * @param email
         * @return message telling if the category was deleted or not
         * @See Category
         */
        post(BASE_PATH + "/removeCategory/:name", (req, res) -> {
            boolean isRemoved = service.removeCategory(req.params(":name"), req.queryParams("email"));
            return isRemoved == true ? "category removed" : "category could not be removed";
        }, gson::toJson);

        /**
         * Adds a Mark to a Category specified by name
         * @param name
         * @param email
         * @return message telling if the Mark was added to the specified Category or not
         * @See Category, Mark
         */
        put(BASE_PATH + "/addMarkToCategory/:categoryName", (req, res) -> {
            Mark mark = gson.fromJson(req.body(), Mark.class);
            String categoryName = req.params(":categoryName");
            String email = req.queryParams("email");
            boolean isOk = service.addToCategory(mark.getUrl(), email, categoryName);
            return isOk ? "mark added to " + categoryName : "mark could not be added to category";
        }, gson::toJson);

        /**
         * Removes a Mark to a Category specified by name
         * @param name
         * @param email
         * @return message telling if the Mark was deleted from the specified Category or not
         * @See Category, Mark
         */
        put(BASE_PATH + "/removeMarkFromCategory/:categoryName", (req, res) -> {
            Mark mark = gson.fromJson(req.body(), Mark.class);
            String categoryName = req.params(":categoryName");
            String email = req.queryParams("email");
            boolean isOk = service.removeFromCategory(mark.getUrl(), email, categoryName);
            return isOk ? "mark removed from " + categoryName : "mark could not be removed from category";
        }, gson::toJson);

        /**
         * Returns an array containing the users Marks
         * @param email
         * @return array of Marks
         * @See Mark
         */
        get(BASE_PATH + "/findAllMarksForUser", (req, res) -> {
            return service.findAllMarksForUser(req.queryParams("email"));
        }, gson::toJson);

        /**
         * Returns an array containing the users Categories
         * @param email
         * @return array of Categories
         * @See Category
         */
        get(BASE_PATH + "/findAllCategoriesForUser", (req, res) -> {
            return service.findAllCategoriesForUser(req.queryParams("email"));
        }, gson::toJson);

    }
}
