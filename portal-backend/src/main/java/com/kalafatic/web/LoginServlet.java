
package com.kalafatic.web;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String username = request.getParameter("username");
			String password = request.getParameter("password");

			// TODO: Validace uživatele
			//if ("admin".equals(username) && "secret".equals(password)) {
			if(UserBean.validate(username, password)) {
				response.sendRedirect("success.jsp");
			} else {
				//RequestDispatcher dispatcher = request.getRequestDispatcher("error.jsp");
				//dispatcher.forward(request, response);
				//response.sendRedirect(request.getContextPath() + "/error.jsp");
				//response.sendRedirect("error.jsp");
				String redirectUrl = "http://" + request.getServerName() + ":18080/portal/error.jsp";
				response.sendRedirect(redirectUrl);

				
			}		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		resp.getWriter().write("<h3>Use POST to log in.</h3>");
	}
}
