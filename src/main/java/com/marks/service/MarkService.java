package com.marks.service;

import com.marks.model.Mark;
import com.marks.store.Store;
import org.apache.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;

import java.util.List;

/**
 * Created by David Jobe on 4/25/16.
 */
public class MarkService {

    Datastore store = Store.getInstance().getDatastore();

    Logger logger = Logger.getLogger(MarkService.class);

    public List<Mark> findAll() {
        return null;
    }

    public Mark findByUrl(String url) {
        return null;
    }

    public Key<Mark> add(Mark mark) {
        return null;
    }

    public void incrementPopularityLevel(String url) {

    }

    public void dencrementPopularityLevel(String url) {

    }

    public void setPublished(String url) {

    }
}
