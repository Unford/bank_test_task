
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Bank app server is running</title>
</head>
<body>
<h1>Добро пожаловать!</h1>
<a href="${pageContext.request.contextPath}/accounts">Счета</a>
<a href="${pageContext.request.contextPath}/transactions">Транзакции</a>
<a href="${pageContext.request.contextPath}/users">Пользователи</a>
<a href="${pageContext.request.contextPath}/banks">Банки</a>

</body>
</html>
