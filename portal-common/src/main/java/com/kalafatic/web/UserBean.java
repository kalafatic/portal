package com.kalafatic.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class UserBean {

    @Resource(lookup = "java:jboss/datasources/MySqlDS")
    private DataSource ds;

    public boolean validate(String username, String password) {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT password FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return BCrypt.checkpw(password, hashedPassword);
            }
            return false; // User not found
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean resetPassword(String username, String newPassword) {
        try (Connection conn = ds.getConnection()) {
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET password=? WHERE username=?");
            ps.setString(1, hashedPassword);
            ps.setString(2, username);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getString(String key, String lang) {
        Locale locale = new Locale(lang);
        ResourceBundle bundle = ResourceBundle.getBundle("com.kalafatic.web.bundles.messages", locale);
        return bundle.getString(key);
    }

    public List<Project> getProjectsByUsername(String username) {
        List<Project> projects = new ArrayList<>();
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name, url FROM projects WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    String url = rs.getString("url");
                    projects.add(new Project(name, url));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return projects;
    }
}
