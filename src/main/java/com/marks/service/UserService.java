package com.marks.service;


import com.marks.model.Mark;
import com.marks.model.User;
import com.marks.store.Store;
import com.marks.util.Hasher;
import com.marks.util.Validator;
import org.apache.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.VerboseJSR303ConstraintViolationException;

import java.util.List;

public class UserService {

    Datastore store = Store.getInstance().getDatastore();
    Validator validator = new Validator();

    Logger logger = Logger.getLogger(UserService.class);


    public List<User> getAllUsers() {
        return store.createQuery(User.class)
                .asList();
    }

    public User getUserByEmail(String email) {
        return store.find(User.class).filter("email =", email).get();
    }

    public boolean signup(User user) {
        try {
            if (getUserByEmail(user.getEmail()) == null) {
                String salt = Hasher.makeSalt();
                if (user.getPassword().length() >= 8 && validator.validatePassword(user.getPassword())) {
                    String storedPassword = Hasher.hash(salt + user.getPassword());
                    user.setSalt(salt);
                    user.setPassword(storedPassword);
                    store.save(user);
                    return true;
                } else {
                    logger.error("password: " + user.getPassword() + " is not valid");
                }
            } else {
                logger.error("user already exists");
            }
        } catch(VerboseJSR303ConstraintViolationException e) {
            logger.error(e);
        }
        return false;
    }

    public User login(String email, String password) {
        User user = getUserByEmail(email);
        if(user != null) {
            String encryptedPassword = Hasher.hash(user.getSalt() + password);
            if(encryptedPassword.equals(user.getPassword())) {
                logger.info("login() -> accepted for user " + email);
                return user;
            }
            logger.info("login() -> wrong password " + password + " should be " + user.getPassword());
            return null;
        }
        logger.info("login() -> user does not exist");
        return null;
    }

    public Mark addMark(String url, String userEmail) {
        User user = getUserByEmail(userEmail);
        Mark mark = new Mark();
        mark.setUrl(url);
        mark.setPublished(false);
        Key<Mark> saved = store.save(mark);
        List<Mark> userMarks = user.getMarks();
        userMarks.add(mark);
        store.save(user);
        return store.getByKey(Mark.class, saved);
    }

}
