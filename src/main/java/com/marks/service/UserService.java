package com.marks.service;


import com.marks.model.Category;
import com.marks.model.Mark;
import com.marks.model.User;
import com.marks.store.Store;
import com.marks.util.Hasher;
import com.marks.util.Validator;
import org.apache.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.VerboseJSR303ConstraintViolationException;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        return store.find(Mark.class).filter("owner =", email).asList();
    }

    public List<Category> findAllCategoriesForUser(String email) {
        User user = getUserByEmail(email);
        return user.getCategories();
    }

    public boolean addCategory(String categoryName, String email) {
        User user = getUserByEmail(email);
        List<Category> categories = user.getCategories();
        categories.add(new Category(categoryName, Collections.emptyList()));
        user.setCategories(categories);
        UpdateOperations<User> ops = store.createUpdateOperations(User.class)
                .set("categories", user.getCategories());
        UpdateResults result = store.update(
                store.createQuery(User.class).field("email").equal(user.getEmail()),
                ops
        );
        logger.info("updated: " + result.getWriteResult().isUpdateOfExisting());
        return result.getWriteResult().isUpdateOfExisting();
    }

    public boolean removeCategory(String categoryName, String email) {
        User user = getUserByEmail(email);
        List<Category> categories = user.getCategories();
        Category category = categories.stream()
                .filter(c -> c.getName().equals(categoryName)).findFirst().get();
        categories.remove(category);
        user.setCategories(categories);
        UpdateOperations<User> ops = store.createUpdateOperations(User.class)
                .set("categories", user.getCategories());
        UpdateResults result = store.update(
                store.createQuery(User.class).field("email").equal(user.getEmail()),
                ops
        );
        logger.info("updated: " + result.getWriteResult().isUpdateOfExisting());
        return result.getWriteResult().isUpdateOfExisting();
    }

    public boolean addToCategory(String url, String email, String categoryName) {
        User user = getUserByEmail(email);
        List<Category> categories = user.getCategories();
        Category category = categories.stream()
                .filter(c -> c.getName().equals(categoryName)).findFirst().orElse(null);
        if(category == null) {
            return false;
        }

        List<String> urls = category.getUrls();
        if (urls.contains(url)) {
            logger.info("mark is already added to category: " + categoryName);
            return false;
        }

        categories = removeMarkFromCategoryIfPresentInAnotherCategory(categories, url);

        logger.info("adding url: " + url + " to category: " + categoryName);
        urls.add(url);
        category.setUrls(urls);
        UpdateOperations<User> ops = store.createUpdateOperations(User.class)
                .set("categories", categories);
        UpdateResults result = store.update(
                store.createQuery(User.class).field("email").equal(user.getEmail()),
                ops
        );
        return result.getWriteResult().isUpdateOfExisting();
    }


    private List<Category> removeMarkFromCategoryIfPresentInAnotherCategory(List<Category> categories, String url) {
        Optional<Category> exists = categories.stream().filter(c -> c.getUrls().contains(url)).findAny();
        if (exists.isPresent()) {
            logger.info("mark is already assigned to a category - removing it");
            String toBeRemoved = exists.get().getUrls().stream().filter(u -> u.equals(url)).findFirst().get();
            exists.get().getUrls().remove(toBeRemoved);
        }
        return categories;
    }

}
