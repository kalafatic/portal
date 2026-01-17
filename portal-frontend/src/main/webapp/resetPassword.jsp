<!DOCTYPE html>
<html>
<head>
    <title>Reset Password</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
  <h2>Reset Password</h2>
  <form action="resetPassword" method="post">
    <input type="text" name="username" placeholder="Username" required /><br/>
    <input type="password" name="newPassword" placeholder="New Password" required /><br/>
    <input type="submit" value="Reset Password" />
  </form>
  <%
    String message = (String) request.getAttribute("message");
    if (message != null) {
  %>
    <p style="color:red;"><%= message %></p>
  <%
    }
  %>
</body>
</html>
