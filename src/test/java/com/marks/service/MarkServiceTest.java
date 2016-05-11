package com.marks.service;

import com.marks.dto.MarkMetaDTO;
import com.marks.model.Mark;
import com.marks.model.User;
import com.marks.store.Store;
import com.marks.util.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.Datastore;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by David Jobe on 4/25/16.
 */
public class MarkServiceTest {

    Datastore store;
    MarkService markService;
    UserService userService;
    User testUser;

    @Before
    public void setUp() throws Exception {
        Store.initialize(Config.DB_TEST);
        markService = new MarkService();
        userService = new UserService();
        store = Store.getInstance().getDatastore();
        testUser = new User();
        testUser.setEmail("tester@tester.com");
        testUser = userService.signin(testUser);
    }

    @After
    public void tearDown() throws Exception {
        store.getDB().dropDatabase();
    }

    @Test
    public void newMarkShouldBeAdded() throws Exception {
        Mark mark = markService.addMark("http://www.enigio.com", "tester@tester.com");
        assertNotNull(mark);
        assertNotNull(markService.findById(mark.getId()));
    }

    @Test
    public void userCannotCreateDuplicateMarks() throws Exception {
        Mark mark = markService.addMark("https://enigio.com", "tester@tester.com");
        Mark mark2 = markService.addMark("https://enigio.com", "tester@tester.com");
        assertNotNull(mark);
        assertNull(mark2);
    }

    @Test
    public void removeMark() throws Exception {
        Mark mark = markService.addMark("https://enigio.com", "tester@tester.com");
        assertTrue(markService.removeMark(mark).getN() == 1);
        assertNull(markService.findById(mark.getId()));
    }

    @Test
    public void assignMetaToMark() throws Exception {
        Mark mark = markService.addMark("https://enigio.com", "tester@tester.com");
        List<String> tags = new ArrayList<>();
        tags.add("time");
        tags.add("tech");
        String thumbnail = "dGVzdGVy";
        MarkMetaDTO meta = new MarkMetaDTO(tags, thumbnail);
        boolean result = markService.assignMetaToMark(mark, meta);
        assertTrue(result);
    }




}