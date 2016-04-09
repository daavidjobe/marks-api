package com.marks;

import com.marks.store.Store;
import org.apache.log4j.Logger;

import static spark.Spark.get;
public class Main {

    final static Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        Store store = new Store("marksDB");

        get("/", (req, res) -> {
            String message = "DB NAME: " + store.getDatabase().getName();
            LOGGER.info("sending message to client: " + message);
            return message;
        });
    }
}
