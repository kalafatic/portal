package com.kalafatic.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    @Resource(lookup = "java:jboss/datasources/MySqlDS")
    private DataSource ds;

    public boolean validate(String username, String password) {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT password FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return password.equals(hashedPassword);//BCrypt.checkpw(password, hashedPassword);
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error validating user: " + username, e);
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
            LOGGER.log(Level.SEVERE, "Error resetting password for user: " + username, e);
            return false;
        }
    }

    public User getUserByUsername(String username) {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT id, username, name, email FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getString("email")
                );
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching user by username: " + username, e);
        }
        return null;
    }

    public boolean updateUser(User user) {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET name=?, email=? WHERE username=?");
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getUsername());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating user: " + user.getUsername(), e);
            return false;
        }
    }
}
