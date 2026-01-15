path/to/filename.js
<%-- src/main/webapp/index.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>JSP + CSS example</title>
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
        <a href="#home">Home</a>
        <a href="#services">Services</a>
        <a href="#contact">Contact</a>
        <a href="login.jsp" style="float:right;">Login</a>
    </div>
    <header>
        <h1>Welcome to my personal website!</h1>
    </header>
    <main>
        <section>
            <p>This website is created for JSP and CSS demonstration purposes.</p>
        </section>
    </main>
    <footer>
        <p>© 2023 My Personal Website</p>
    </footer>
</body>
</html>
