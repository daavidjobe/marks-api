package com.marks.service;

import com.marks.dto.MarkDTO;
import com.marks.model.Mark;
import com.marks.model.MarkMeta;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<MarkDTO> findPublishedMarks() {
        List<Mark> marks = store.createQuery(Mark.class).filter("published", true).order("-creationDate").asList();
        return marks.stream().map(mark -> {
            MarkMeta meta = getMetaForMark(mark.getUrl());
            return new MarkDTO(mark.getUrl(), mark.getThumbnail(),
                    mark.getCreationDate(), meta.getPromotions(), meta.getDemotions());
        }).collect(Collectors.toList());
    }

    public List<MarkDTO> findMostPopularMarks() {
        List<Mark> marks = store.createQuery(Mark.class).filter("published", true).asList();

        Comparator<MarkDTO> byPromotions = (m1, m2) -> Integer.compare(
                m2.getPromotions(), m1.getPromotions());

        return marks.stream().map(mark -> {
            MarkMeta meta = getMetaForMark(mark.getUrl());
            return new MarkDTO(mark.getUrl(), mark.getThumbnail(),
                    mark.getCreationDate(), meta.getPromotions(), meta.getDemotions());
        }).sorted(byPromotions).limit(50).collect(Collectors.toList());
    }

    public Mark findById(ObjectId id) {
        return store.find(Mark.class).field("_id").equal(id).get();
    }

    public Mark findByKey(Key<Mark> key) {
        return store.getByKey(Mark.class, key);
    }

    public Mark findUsersMarkByUrl(String email, String url) {
        return store.createQuery(Mark.class).filter("owner", email).filter("url", url).get();
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

    public WriteResult removeOwnerFromMark(Mark mark) {
        UpdateOperations<Mark> ops = store.createUpdateOperations(Mark.class)
                .set("owner", "");
        UpdateResults result = store.update(
                store.createQuery(Mark.class).field("_id").equal(mark.getId()),
                ops);
        return result.getWriteResult();
    }


    public boolean assignMetaToMark(Mark mark, MarkDTO meta) {
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

    public MarkMeta getMetaForMark(String url) {
        MarkMeta meta = store.find(MarkMeta.class).field("url").equal(url).get();
        if(meta == null) {
            meta = new MarkMeta(url);
            store.save(meta);
        }
        return meta;
    }

    public boolean promoteMark(String email, String url, boolean isPromote) {
        MarkMeta meta = getMetaForMark(url);

        if(userHasInteracted(email, meta)) {
            return false;
        }
        List<String> userEmails = meta.getUserEmails();
        userEmails.add(email);
        UpdateOperations<MarkMeta> ops = null;
        if(isPromote) {
            int promotes = meta.getPromotions();
            ops = store.createUpdateOperations(MarkMeta.class)
                    .set("promotions", ++promotes).set("userEmails", userEmails);
        } else {
            int demotions = meta.getDemotions();
            ops = store.createUpdateOperations(MarkMeta.class)
                    .set("demotions", ++demotions).set("userEmails", userEmails);;
        }
        UpdateResults result = store.update(
                store.createQuery(MarkMeta.class).field("url").equal(meta.getUrl()),
                ops);
        return result.getWriteResult().isUpdateOfExisting();
    }

    public boolean userHasInteracted(String email, MarkMeta meta) {
        Optional<String> user = meta.getUserEmails().stream().filter(e -> e.equals(email)).findFirst();
        if(user.isPresent()) {
            return true;
        }
        return false;
    }

}
