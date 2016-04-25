package com.marks;

import com.marks.controller.UserController;
import com.marks.service.UserService;
import com.marks.store.Store;
import com.marks.util.Config;
import org.apache.log4j.Logger;

import static spark.Spark.get;

public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        Store.initialize(Config.DB_PROD);

        UserController userController = new UserController(new UserService());

        get("/api/", (req, res) -> "Marks REST API");


    }
}
