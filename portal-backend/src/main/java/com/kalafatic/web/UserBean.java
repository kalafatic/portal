package com.kalafatic.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserBean {
	
	public static boolean validate(String username, String password) {
		
		try (Connection conn = DriverManager
				.getConnection("jdbc:mysql://localhost:3306/portal?user=petr&password=traged")) {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
			ps.setString(1, username);
			ps.setString(2, password);

			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
