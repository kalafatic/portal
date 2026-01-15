path/to/filename.js
<%-- src/main/webapp/index.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
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
        <a href="#home">Domů</a>
        <a href="#services">Služby</a>
        <a href="#contact">Kontakt</a>
        <a href="login.jsp" style="float:right;">Přihlásit se</a>
    </div>
    <header>
        <h1>Vítej na mém osobním webu!</h1>
    </header>
    <main>
        <section>
            <p>Tento web je vytvořen pro ukázku JSP a CSS.</p>
        </section>
    </main>
    <footer>
        <p>© 2023 Můj Osobní Web</p>
    </footer>
</body>
</html>
