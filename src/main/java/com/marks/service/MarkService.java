package com.marks.service;

import com.marks.dto.MarkMetaDTO;
import com.marks.model.Mark;
import com.marks.store.Store;
import com.marks.util.Validator;
import com.mongodb.WriteResult;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.VerboseJSR303ConstraintViolationException;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.Collections;
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

    public List<Mark> findPublishedMarks() {
        return store.createQuery(Mark.class).filter("published", true).order("-creationDate").asList();
    }

    public Mark findById(ObjectId id) {
        return store.find(Mark.class).field("_id").equal(id).get();
    }

    public Mark findByKey(Key<Mark> key) {
        return store.getByKey(Mark.class, key);
    }

    public Mark addMark(String url, String email) {
        if(isDuplicate(url, email)) {
            logger.info("mark with url: " + url + "is already added by user");
            return null;
        }
        try {
            Mark mark = new Mark(url);
            mark.setPublished(false);
            mark.setOwner(email);
            store.save(mark);
            return store.find(Mark.class).field("url").equal(url).get();
        } catch(VerboseJSR303ConstraintViolationException e) {
            logger.error("mark with invalid url is not accepted", e);
            return null;
        }
    }

    private boolean isDuplicate(String url, String email) {
        List<Mark> userMarks = userService.findAllMarksForUser(email);
        return userMarks.stream().anyMatch(m -> m.getUrl().equals(url));
    }

    public WriteResult removeMark(Mark mark) {
        return store.delete(mark.getClass(), mark.getId());
    }


    public boolean assignMetaToMark(Mark mark, MarkMetaDTO meta) {
        List<String> tags = meta.getTags() != null ? meta.getTags() : Collections.emptyList();
        UpdateOperations<Mark> ops = store.createUpdateOperations(Mark.class)
                .set("tags", tags).set("thumbnail", meta.getThumbnail())
                .set("published", true);
        UpdateResults result = store.update(
                store.createQuery(Mark.class).field("_id").equal(mark.getId()),
                ops);
        logger.info("update result: " + result);
        return result.getWriteResult().isUpdateOfExisting();
    }
}
