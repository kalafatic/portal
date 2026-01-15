portal-frontend/src/main/webapp/index.jsp
<%-- src/main/webapp/index.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>JSP + CSS ukázka</title>
    <link rel="stylesheet" href="css/style.css" />
    <link rel="stylesheet" href="css/theme.css" />
    <style>
        .menu {
            background-color: #333;
            overflow: hidden;
        }
        .menu a {
            float: left;
            display: block;
            color: #f2f2f2;
            text-align: center;
            padding: 14px 20px;
            text-decoration: none;
        }
        .menu a:hover {
            background-color: #ddd;
            color: black;
        }
    </style>
</head>
<body>
    <div class="menu">
        <a href="#home" lang="en">Home</a>
        <a href="#services" lang="en">Services</a>
        <a href="#contact" lang="en">Contact</a>
        <a href="login.jsp" style="float:right;" lang="en">Login</a>
    </div>
    <header>
        <h1><%= getString("Welcome to my personal website!") %></h1>
    </header>
    <main>
        <section>
            <p><%= getString("This web is created for JSP and CSS example.") %></p>
        </section>
    </main>
    <footer>
        <p><%= getString("© 2023 My Personal Website") %></p>
    </footer>
</body>
</html>
