package com.marks.service;

import com.marks.model.Mark;
import com.marks.store.Store;
import com.marks.util.Validator;
import com.mongodb.WriteResult;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;

import java.util.List;

/**
 * Created by David Jobe on 4/25/16.
 */
public class MarkService {

    Datastore store = Store.getInstance().getDatastore();
    UserService userService = new UserService();
    Logger logger = Logger.getLogger(MarkService.class);
    Validator validator = new Validator();

    public List<Mark> findAll() {
        return store.createQuery(Mark.class)
                .asList();
    }

    public Mark findById(String id) {
        ObjectId objId = new ObjectId(id);
        return store.find(Mark.class).field("id").equal(objId).get();
    }

    public Mark findByKey(Key<Mark> key) {
        return store.getByKey(Mark.class, key);
    }

    public Mark addMark(String url, String email) {
        if(!validator.validateUrl(url)) {
            logger.info(url + " is invalid");
            return null;
        }
        if(isDuplicate(url, email)) {
            logger.info("mark with url: " + url + "is already added by user");
            return null;
        }
        Mark mark = new Mark(url);
        mark.setPublished(false);
        mark.setOwner(email);
        store.save(mark);
        return store.find(Mark.class).field("url").equal(url).get();
    }

    private boolean isDuplicate(String url, String email) {
        List<Mark> userMarks = userService.findAllMarksForUser(email);
        return userMarks.stream().anyMatch(m -> m.getUrl().equals(url));
    }

    public WriteResult removeMark(Mark mark) {
        return store.delete(mark);
    }


}
