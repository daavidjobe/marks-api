package com.marks.service;


import com.marks.model.Category;
import com.marks.model.Mark;
import com.marks.model.User;
import com.marks.store.Store;
import com.marks.util.Hasher;
import com.marks.util.Validator;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.VerboseJSR303ConstraintViolationException;
import org.mongodb.morphia.query.Query;

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
                boolean isValid = validator.validatePassword(user.getPassword());
                logger.info("isValid: " + isValid);
                if (user.getPassword().length() >= 8 && isValid) {
                    String storedPassword = Hasher.hash(salt + user.getPassword());
                    user.setSalt(salt);
                    user.setPassword(storedPassword);
                    Category category = new Category();
                    category.setName("all");
                    user.getCategories().add(category);
                    store.save(user);
                    return true;
                } else {
                    logger.error("password: " + user.getPassword() + " is not valid");
                }
            } else {
                logger.error("user already exists");
            }
        } catch (VerboseJSR303ConstraintViolationException e) {
            logger.error(e);
        }
        return false;
    }

    public User login(String email, String password) {
        User user = getUserByEmail(email);
        if (user != null) {
            String encryptedPassword = Hasher.hash(user.getSalt() + password);
            if (encryptedPassword.equals(user.getPassword())) {
                logger.info("login() -> accepted for user " + email);
                return user;
            }
            logger.info("login() -> wrong password " + password + " should be " + user.getPassword());
            return null;
        }
        logger.info("login() -> user does not exist");
        return null;
    }

    public List<Mark> findAllMarksForUser(String email) {
        Query<Mark> query = store.createQuery(Mark.class);
        query.field("owner").equal(email);
        return query.asList();
    }

    public List<Category> findAllCategoriesForUser(String email) {
        User user = getUserByEmail(email);
        return user.getCategories();
    }

    public boolean addCategory(String categoryName, String email) {
        User user = getUserByEmail(email);
        boolean isOk = true;
        List<Category> categories = user.getCategories();
        logger.info(categories);
        for (Category c : categories) {
            if (c.getName().equals(categoryName)) {
                logger.info("category already exists");
                isOk = false;
            }
        }
        Category category = new Category(categoryName);
        categories.add(category);
        user.setCategories(categories);
        store.save(user);
        return isOk;
    }

    public boolean addToCategory(Mark mark, String email, String categoryName) {
        User user = getUserByEmail(email);
        boolean isOk = false;
        List<Category> categories = user.getCategories();
        logger.info("adding mark to category: '" + categoryName + "'");
        for (Category c : categories) {
            if (c.getName().equals(categoryName)) {
                if (!c.getMarkIds().contains(mark.getId())) {
                    isOk = true;
                    List<ObjectId> markIds = c.getMarkIds();
                    markIds.add(mark.getId());
                    c.setMarkIds(markIds);
                    user.setCategories(categories);
                    store.save(user);
                } else {
                    isOk = false;
                }
            }
        }
        return isOk;
    }
}
