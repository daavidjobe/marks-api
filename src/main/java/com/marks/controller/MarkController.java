package com.marks.controller;

import com.google.gson.Gson;
import com.marks.dto.MarkMetaDTO;
import com.marks.model.Mark;
import com.marks.service.MarkService;
import com.marks.util.Config;
import com.mongodb.WriteResult;
import org.apache.log4j.Logger;

import static spark.Spark.*;

/**
 * Created by David Jobe on 5/5/16.
 */
public class MarkController {

    private Gson gson = new Gson();
    public static final String BASE_PATH = Config.ROOT_PATH + "/marks";

    Logger logger = Logger.getLogger(MarkController.class);

    public MarkController(MarkService service) {

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
         * @return List of Marks
         * @see Mark
         */
        get(BASE_PATH + "/findAll", (req, res) -> {
            return service.findAll();
        }, gson::toJson);

        /**
         * @return list of Marks where published boolean flag is equal to true
         * @see Mark
         */
        get(BASE_PATH + "/findPublishedMarks", (req, res) -> {
            return service.findPublishedMarks();
        }, gson::toJson);

        /**
         * Adds a Mark to User
         * @param email
         * @return Mark if it was added. Otherwise a message
         * @see Mark
         */
        post(BASE_PATH + "/addMark", (req, res) -> {
            String url = req.body();
            Mark mark = service.addMark(url, req.queryParams("email"));
            if(mark == null) {
                res.status(406);
                return "mark could not be created";
            }
            return mark;
        }, gson::toJson);

        /**
         * Removes a Mark from User
         * @return boolean
         * @see Mark
         */
        put(BASE_PATH + "/removeMark", (req, res) -> {
            Mark mark = gson.fromJson(req.body(), Mark.class);
            WriteResult result = service.removeOwnerFromMark(mark);
            return result.isUpdateOfExisting();
        }, gson::toJson);

        /**
         * Uses a Web Scraper to crawl website specified bu url. The scraper then parses
         * the website and extracts useful data. It also captures a screenshot wich is used
         * to create a thumbnail. On completion the data and thumbnail gets added to the Mark
         * Document.
         * @return Data & base64 thumbnail
         */
        post(BASE_PATH + "/assignMetaToMark", (req, res) -> {
            MarkMetaDTO meta = gson.fromJson(req.body(), MarkMetaDTO.class);
            Mark mark = meta.getMark();
            return service.assignMetaToMark(mark, meta) ? meta : "could not fetch meta";
        }, gson::toJson);

    }
}
