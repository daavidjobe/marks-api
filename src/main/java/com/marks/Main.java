package com.marks;

import com.marks.controller.UserController;
import com.marks.service.UserService;
import com.marks.store.Store;
import com.marks.util.Config;
import org.apache.log4j.Logger;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        Store.initialize(Config.DB_PROD);

        new UserController(new UserService());

        get("/api/", (req, res) -> "Marks REST API");


    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }
}
