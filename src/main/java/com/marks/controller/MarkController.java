package com.marks.controller;

import com.google.gson.Gson;
import com.marks.model.Mark;
import com.marks.service.MarkService;
import com.marks.util.Config;
import com.mongodb.WriteResult;
import org.apache.log4j.Logger;

import static spark.Spark.get;
import static spark.Spark.post;
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
        }, gson::toJson);

        post(BASE_PATH + "/addMark", (req, res) -> {
            String url = req.body();
            Mark mark = service.addMark(url, req.queryParams("email"));
            if(mark == null) {
                res.status(406);
                return "mark could not be created";
            }
            return mark;
        }, gson::toJson);

        put(BASE_PATH + "/removeMark", (req, res) -> {
            Mark mark = gson.fromJson(req.body(), Mark.class);
            WriteResult result = service.removeMark(mark);
            return result.isUpdateOfExisting();
        }, gson::toJson);

    }
}
