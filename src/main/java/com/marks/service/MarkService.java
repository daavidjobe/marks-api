package com.marks.service;

import com.marks.model.Mark;
import com.marks.store.Store;
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

    public List<Mark> findAll() {
        return store.createQuery(Mark.class)
                .asList();
    }

    public Mark findById(String id) {
        ObjectId objId = new ObjectId(id);
        return store.find(Mark.class).field("id").equal(objId).get();
    }

    public Mark addMark(String url, String email) {
        Mark mark = new Mark();
        mark.setUrl(url);
        mark.setPublished(false);
        mark.setOwner(email);
        Key<Mark> saved = store.save(mark);
        return store.getByKey(Mark.class, saved);
    }

    public WriteResult removeMark(Mark mark) {
        return store.delete(mark);
    }


}
