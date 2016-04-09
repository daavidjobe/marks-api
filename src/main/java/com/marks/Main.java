package com.marks;

import org.apache.log4j.Logger;

import static spark.Spark.get;
public class Main {

    final static Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        get("/", (req, res) -> {
            String message = "marks";
            LOGGER.info("sending message to client: " + message);
            return "marks";
        });
    }
}
