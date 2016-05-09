package com.marks.controller;

import com.google.gson.Gson;
import com.marks.dto.MarkMetaDTO;
import com.marks.model.Mark;
import com.marks.service.MarkService;
import com.marks.util.Config;
import com.mongodb.WriteResult;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static spark.Spark.*;

/**
 * Created by David Jobe on 5/5/16.
 */
public class MarkController {

    private Gson gson = new Gson();
    public static final String BASE_PATH = Config.ROOT_PATH + "/marks";

    Logger logger = Logger.getLogger(MarkController.class);

    public MarkController(MarkService service) {

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
            WriteResult result = service.removeMark(mark);
            return result.isUpdateOfExisting();
        }, gson::toJson);

        /**
         * Uses a Web Scraper to crawl website specified bu url. The scraper then parses
         * the website and extracts useful data. It also captures a screenshot wich is used
         * to create a thumbnail. On completion the data and thumbnail gets added to the Mark
         * Document.
         * @return Data & base64 thumbnail
         */
        post(BASE_PATH + "/fetchMarkMeta", (req, res) -> {
            Mark mark = gson.fromJson(req.body(), Mark.class);
            URL scraper = new URL(Config.SCRAPER_URL + "/scraper?url=" + mark.getUrl());
            HttpURLConnection con = (HttpURLConnection) scraper.openConnection();
            int responseCode = con.getResponseCode();
            logger.info("Sending 'GET' request to URL : " + scraper);
            logger.info("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            MarkMetaDTO meta = gson.fromJson(response.toString(), MarkMetaDTO.class);
            service.assignMetaToMark(mark, meta);
            return service.assignMetaToMark(mark, meta) ? meta : "could not fetch meta";
        }, gson::toJson);

    }
}
