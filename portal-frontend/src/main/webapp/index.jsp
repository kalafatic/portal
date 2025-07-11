<%-- src/main/webapp/index.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>JSP + CSS ukázka</title>
    <link rel="stylesheet" href="css/style.css" />
</head>
<body>

<form action="login.jsp" method="get">
    <button type="submit">Přihlásit se</button>
</form>
<button onclick="location.href='login.jsp'">Přihlásit se</button>
<a href="login.jsp" class="btn">Přihlásit se</a>
    <h1>Seznam uživatelů</h1>

    <table>
        <thead>
            <tr>
                <th>Jméno</th>
                <th>Email</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>Petr Novák</td>
                <td>petr@example.com</td>
            </tr>
            <tr>
                <td>Jana Horáková</td>
                <td>jana@example.com</td>
            </tr>
        </tbody>
    </table>
</body>
</html>
