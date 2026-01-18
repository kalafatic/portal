package com.kalafatic.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;

public class UserBean {

	public static boolean validate(String username, String password) {
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:jboss/datasources/MySqlDS");
			try (Connection conn = ds.getConnection();
					PreparedStatement ps = conn.prepareStatement("SELECT password FROM users WHERE username=?")) {
				ps.setString(1, username);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						String hashedPassword = rs.getString("password");
						return BCrypt.checkpw(password, hashedPassword);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false; // User not found or error
	}

	public static boolean resetPassword(String username, String newPassword) {
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:jboss/datasources/MySqlDS");
			try (Connection conn = ds.getConnection();
					PreparedStatement ps = conn.prepareStatement("UPDATE users SET password=? WHERE username=?")) {
				String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
				ps.setString(1, hashedPassword);
				ps.setString(2, username);
				int rowsAffected = ps.executeUpdate();
				return rowsAffected > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getString(String key, String lang) {
		Locale locale = new Locale(lang);
		ResourceBundle bundle = ResourceBundle.getBundle("com.kalafatic.web.bundles.messages", locale);
		return bundle.getString(key);
	}
}
