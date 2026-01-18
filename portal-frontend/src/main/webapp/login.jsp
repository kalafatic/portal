<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
  <h2>Login</h2>
  <%
    String message = (String) request.getAttribute("message");
    if (message != null) {
  %>
    <p style="color:red;"><%= message %></p>
  <%
    }
  %>
  <form action="/portal/login" method="post">
    <input type="text" name="username" placeholder="Username" required /><br/>
    <input type="password" name="password" placeholder="Password" required /><br/>
    <input type="submit" value="Login" />
  </form>
  <a href="resetPassword.jsp">Forgot Password?</a>
</body>
</html>
