package com.kalafatic.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String username = request.getParameter("username");
            String newPassword = request.getParameter("newPassword");

            // This method will be implemented in a later step
            boolean success = UserBean.resetPassword(username, newPassword);

            String message;
            if (success) {
                message = "Password reset successfully!";
            } else {
                message = "Failed to reset password. User not found.";
            }

            request.setAttribute("message", message);
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "An error occurred.");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
        }
    }
}
