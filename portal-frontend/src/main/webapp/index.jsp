<%-- src/main/webapp/index.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="userBean" class="com.kalafatic.web.UserBean" scope="request" />
<c:set var="lang" value="${param.lang != null ? param.lang : 'en'}" />
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${userBean.getString("title", lang)}</title>
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
        <a href="#home">${userBean.getString("home", lang)}</a>
        <a href="#services">${userBean.getString("services", lang)}</a>
        <a href="#contact">${userBean.getString("contact", lang)}</a>
        <a href="login.jsp" style="float:right;">${userBean.getString("login", lang)}</a>
    </div>
    <header>
        <h1>${userBean.getString("welcome", lang)}</h1>
    </header>
    <main>
        <section>
            <p>${userBean.getString("description", lang)}</p>
        </section>
    </main>
    <footer>
        <p>${userBean.getString("copyright", lang)}</p>
    </footer>
</body>
</html>
