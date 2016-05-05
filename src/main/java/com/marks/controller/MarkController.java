package com.marks.controller;

import com.google.gson.Gson;
import com.marks.model.Mark;
import com.marks.service.MarkService;
import com.marks.util.Config;
import com.marks.util.JsonTransformer;
import com.mongodb.WriteResult;
import org.apache.log4j.Logger;

import static spark.Spark.get;
import static spark.Spark.put;

/**
 * Created by David Jobe on 5/5/16.
 */
public class MarkController {

    private Gson gson = new Gson();
    public static final String BASE_PATH = Config.ROOT_PATH + "/marks";

    Logger logger = Logger.getLogger(MarkController.class);

    public MarkController(MarkService service) {

        get(BASE_PATH + "/findAll", (req, res) -> {
            return service.findAll();
        }, new JsonTransformer());

        put(BASE_PATH + "/addMark", (req, res) -> {
            return service.addMark(req.queryParams("url"), req.queryParams("email"));
        }, new JsonTransformer());

        put(BASE_PATH + "/removeMark", (req, res) -> {
            Mark mark = gson.fromJson(req.body(), Mark.class);
            WriteResult result = service.removeMark(mark);
            return result.isUpdateOfExisting();
        }, new JsonTransformer());

    }
}
