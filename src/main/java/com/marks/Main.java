package com.marks;

import com.marks.controller.MarkController;
import com.marks.controller.UserController;
import com.marks.service.MarkService;
import com.marks.service.UserService;
import com.marks.sockets.MarkSocket;
import com.marks.store.Store;
import com.marks.util.Config;
import org.apache.log4j.Logger;

import static spark.Spark.*;

public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        Store.initialize(Config.DB_PROD);

        webSocket("/api/socket", MarkSocket.class);

        new UserController(new UserService());
        new MarkController(new MarkService());

        get(Config.ROOT_PATH, (req, res) -> "Marks REST API");

        get(Config.ROOT_PATH + "/port", (req, res) -> Config.PORT);

    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            Config.SCRAPER_URL = "http://marks-scraper.herokuapp.com";
            Config.PORT = Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return Config.PORT;
    }
}
