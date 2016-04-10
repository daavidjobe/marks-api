package com.marks;

import com.marks.controller.UserController;
import com.marks.store.Store;
import org.apache.log4j.Logger;

import static spark.Spark.get;

public class Main {

    final static Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        Store store = Store.getInstance();

        UserController userController = new UserController();
        userController.setupEndpoints();

        get("/", (req, res) -> {
            return "marks REST API";
        });


    }
}
