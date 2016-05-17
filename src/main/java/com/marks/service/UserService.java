package com.marks.service;


import com.marks.model.Category;
import com.marks.model.Mark;
import com.marks.model.User;
import com.marks.store.Store;
import org.apache.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserService {

    Datastore store = Store.getInstance().getDatastore();

    Logger logger = Logger.getLogger(UserService.class);


    public List<User> getAllUsers() {
        return store.createQuery(User.class)
                .asList();
    }

    public User getUserByEmail(String email) {
        return store.find(User.class).filter("email =", email).get();
    }


    // sign in user. If it´s the first time a new user will be stored

    public User signin(User user) {
        User storedUser = getUserByEmail(user.getEmail());
        if (storedUser == null) {
            store.save(user);
        }
        return getUserByEmail(user.getEmail());
    }

    public List<Mark> findAllMarksForUser(String email) {
        return store.find(Mark.class).filter("owner =", email).order("-creationDate").asList();
    }

    public List<Category> findAllCategoriesForUser(String email) {
        User user = getUserByEmail(email);
        return user.getCategories();
    }

    // Adds a user defined category

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

    // Removes a user defined category

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

    // Add the mark to a user defined category

    public boolean addToCategory(String url, String email, String categoryName) {
        User user = getUserByEmail(email);
        List<Category> categories = user.getCategories();
        Category category = categories.stream()
                .filter(c -> c.getName().equals(categoryName)).findFirst().orElse(null);
        if (category == null) {
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

    // Removes a Mark from A user defined category

    public boolean removeFromCategory(String url, String email, String categoryName) {
        User user = getUserByEmail(email);
        List<Category> categories = user.getCategories();
        Optional<Category> category = categories.stream().filter(c -> c.getName().equals(categoryName)).findAny();
        logger.info("removing url: " + url + " from category: " + categoryName);
        if (category.isPresent()) {
            String toBeRemoved = category.get().getUrls().stream().filter(u -> u.equals(url)).findFirst().get();
            List<String> urls = category.get().getUrls();
            urls.remove(toBeRemoved);
            category.get().setUrls(urls);
            UpdateOperations<User> ops = store.createUpdateOperations(User.class)
                    .set("categories", categories);
            UpdateResults result = store.update(
                    store.createQuery(User.class).field("email").equal(user.getEmail()),
                    ops
            );
            return result.getWriteResult().isUpdateOfExisting();
        }
        return false;
    }


    // Helper method for removing Mark from a Category wich it is already placed in

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
