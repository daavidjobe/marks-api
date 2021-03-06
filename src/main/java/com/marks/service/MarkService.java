package com.marks.service;

import com.marks.dto.MarkDTO;
import com.marks.model.Mark;
import com.marks.model.MarkMeta;
import com.marks.store.Store;
import com.mongodb.WriteResult;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.VerboseJSR303ConstraintViolationException;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    public List<MarkDTO> findPublishedMarks(String email) {
        List<Mark> marks = store.createQuery(Mark.class).filter("published", true)
                .order("-creationDate").limit(200).asList();
        return marks.stream().map(mark -> {
            MarkMeta meta = getMetaForMark(mark.getUrl());
            boolean hasInteracted = userHasInteracted(email, meta);
            return new MarkDTO(mark.getUrl(), mark.getThumbnail(),
                    mark.getCreationDate(), meta.getPromotions(), meta.getDemotions(),
                    hasInteracted);
        }).filter(distinctByKey(MarkDTO::getUrl)).collect(Collectors.toList());
    }

    public List<MarkDTO> findMostPopularMarks(String email) {
        List<Mark> marks = store.createQuery(Mark.class).filter("published", true).asList();
        Comparator<MarkDTO> byPromotions = (m1, m2) -> Integer.compare(
                m2.getPromotions(), m1.getPromotions());
        return marks.stream().map(mark -> {
            MarkMeta meta = getMetaForMark(mark.getUrl());
            boolean hasInteracted = userHasInteracted(email, meta);
            return new MarkDTO(mark.getUrl(), mark.getThumbnail(),
                    mark.getCreationDate(), meta.getPromotions(), meta.getDemotions(),
                    hasInteracted);
        }).filter(distinctByKey(MarkDTO::getUrl)).sorted(byPromotions).limit(100).collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T,Object> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public Mark findById(ObjectId id) {
        return store.find(Mark.class).field("_id").equal(id).get();
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

    // User should not be able to create Mark for same url twice
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

    // Updates Mark data with response from Scraper

    public boolean assignThumbnailToMark(Mark mark, MarkDTO meta) {
        UpdateOperations<Mark> ops = store.createUpdateOperations(Mark.class)
                .set("thumbnail", meta.getThumbnail())
                .set("published", true);
        UpdateResults result = store.update(
                store.createQuery(Mark.class).field("_id").equal(mark.getId()),
                ops);
        logger.info("update result: " + result);
        return result.getWriteResult().isUpdateOfExisting();
    }

    // Retrieves additional data from Mark

    public MarkMeta getMetaForMark(String url) {
        MarkMeta meta = store.find(MarkMeta.class).field("url").equal(url).get();
        if(meta == null) {
            meta = new MarkMeta(url);
            store.save(meta);
        }
        return meta;
    }

    //Promote or demote a mark specified by boolean flag

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
        if(user.isPresent() && email != null) {
            return true;
        }
        return false;
    }

}
