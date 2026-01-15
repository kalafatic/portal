path/to/filename.js
<%-- src/main/webapp/index.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>JSP + CSS ukázka</title>
<style>
body {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 100vh;
	margin: 0;
	background-color: #f4f4f4;
}

.btn {
	padding: 10px 20px;
	font-size: 16px;
	color: white;
	background-color: #007bff;
	border: none;
	cursor: pointer;
	text-decoration: none;
	margin: 10px;
}

.btn:hover {
	background-color: #0056b3;
}
</style>
</head>
<body>

	<form action="login.jsp" method="get">
		<button type="submit" class="btn">Přihlásit se</button>
	</form>
	<button onclick="location.href='login.jsp'" class="btn">Přihlásit
		se</button>
	<a href="login.jsp" class="btn">Přihlásit se</a>
</body>
</html>
