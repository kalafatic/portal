package com.kalafatic.web;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/users")
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        List<User> users = new ArrayList<>();

        try {
            // Připojení k DB
            Class.forName("com.mysql.cj.jdbc.Driver");
        	Connection conn = DriverManager
    				.getConnection("jdbc:mysql://localhost:3306/portal?user=petr&password=traged");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name, email FROM users");

            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email")
                ));
            }

            conn.close();

        } catch (Exception e) {
            throw new ServletException(e);
        }

        req.setAttribute("users", users);
        req.getRequestDispatcher("/users.jsp").forward(req, resp);
    }
}

