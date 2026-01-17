<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
  <h2>Login</h2>
  <form action="http://localhost:9080/portal/login" method="post">
    <input type="text" name="username" placeholder="Username" required /><br/>
    <input type="password" name="password" placeholder="Password" required /><br/>
    <input type="submit" value="Login" />
  </form>
</body>
</html>
