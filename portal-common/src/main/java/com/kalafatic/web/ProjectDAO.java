package com.kalafatic.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

@ApplicationScoped
public class ProjectDAO {

    private static final Logger LOGGER = Logger.getLogger(ProjectDAO.class.getName());

    @Resource(lookup = "java:jboss/datasources/MySqlDS")
    private DataSource ds;

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
            LOGGER.log(Level.SEVERE, "Error fetching projects for user: " + username, e);
        }
        return projects;
    }

    public boolean addProject(String username, String name, String url) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO projects (username, name, url) VALUES (?, ?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, name);
            ps.setString(3, url);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding project for user: " + username, e);
            return false;
        }
    }

    public boolean deleteProject(String username, String name) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM projects WHERE username = ? AND name = ?")) {
            ps.setString(1, username);
            ps.setString(2, name);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting project for user: " + username, e);
            return false;
        }
    }
}
