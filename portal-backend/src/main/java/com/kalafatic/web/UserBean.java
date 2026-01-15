package com.kalafatic.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.ResourceBundle;

public class UserBean {

			ResultSet rs = ps.executeQuery();
			return rs.next();
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
}
