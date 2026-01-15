package com.kalafatic.web;

import java.sql.*;

public class UserBean {

    public static boolean validate(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/portal?user=petr&password=traged")) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getStringForLanguage(String username, String password, String languageKey) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/portal?user=petr&password=traged")) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String language = rs.getString("language"); // Assuming 'language' is a column in your users table
                switch (language.toLowerCase()) {
                    case "en":
                    case "english":
                        return "Hello"; // Default to English greeting
                    case "es":
                    case "spanish":
                        return "Hola"; // Example for Spanish
                    default:
                        return "Hello"; // Default fallback
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Hello"; // Default fallback if validation fails or language not found
    }
}
