package com.kalafatic.web;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UserBean {

    @Inject
    private UserDAO userDAO;

    @Inject
    private ProjectDAO projectDAO;

    public boolean validate(String username, String password) {
        return userDAO.validate(username, password);
    }

    public boolean resetPassword(String username, String newPassword) {
        return userDAO.resetPassword(username, newPassword);
    }

    public String getString(String key, String lang) {
        Locale locale = new Locale(lang);
        ResourceBundle bundle = ResourceBundle.getBundle("com.kalafatic.web.bundles.messages", locale);
        return bundle.getString(key);
    }

    public List<Project> getProjectsByUsername(String username) {
        return projectDAO.getProjectsByUsername(username);
    }

    public boolean addProject(String username, String name, String url) {
        return projectDAO.addProject(username, name, url);
    }

    public boolean deleteProject(String username, String name) {
        return projectDAO.deleteProject(username, name);
    }

    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }

    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }
}
